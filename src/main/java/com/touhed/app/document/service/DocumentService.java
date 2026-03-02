package com.touhed.app.document.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.touhed.app.document.DocumentDTO;
import com.touhed.app.document.DocumentType;
import com.touhed.app.document.model.Document;
import com.touhed.app.document.repository.DocumentRepository;
import com.touhed.app.util.enums.EntityName;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final Cloudinary cloudinary; // Injected via Configuration
    private final DocumentRepository documentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Uploads a file from a MultipartFile (Web Upload)
     */
    public Document uploadDocument(MultipartFile file, DocumentType documentType,
                                   EntityName entityName, Long entityId) throws IOException {

        String folderPath = buildFolderPath(documentType.getDirectory(), generateSubDirectory());

        // Cloudinary upload options
        Map params = ObjectUtils.asMap(
                "folder", folderPath,
                "public_id", getFileNameWithoutExtension(file.getOriginalFilename()),
                "resource_type", "auto",
                "use_filename", true,
                "unique_filename", true
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        return saveDocumentMetadata(uploadResult, documentType, entityName, entityId);
    }

    /**
     * Uploads a file from a DTO (Byte Array)
     */
    public Document uploadDocument(DocumentDTO documentDTO, Long entityId, EntityName entityName) {
        String folderPath = buildFolderPath(documentDTO.getDocumentType().getDirectory(), generateSubDirectory());

        try {
            Map params = ObjectUtils.asMap(
                    "folder", folderPath,
                    "public_id", getFileNameWithoutExtension(documentDTO.getFileName()),
                    "resource_type", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(documentDTO.getFileData(), params);

            return saveDocumentMetadata(uploadResult, documentDTO.getDocumentType(), entityName, entityId);
        } catch (IOException e) {
            log.error("Cloudinary upload failed", e);
            throw new RuntimeException("Failed to upload to Cloudinary", e);
        }
    }

    /**
     * Generates a URL for the document.
     * Cloudinary handles CDN delivery automatically.
     */
    public String getDocumentUrl(Document document) {
        // Cloudinary uses the Public ID for URL generation
        // Format: folder/subfolder/filename
        String publicId = buildCloudinaryPublicId(document);

        return cloudinary.url()
                .resourceType("auto")
                .secure(true)
                .generate(publicId);
    }

    /**
     * Downloads document as byte array
     */
    public byte[] downloadDocument(Document document) throws IOException {
        String url = getDocumentUrl(document);
        try (java.io.InputStream is = new URL(url).openStream()) {
            return is.readAllBytes();
        }
    }

    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found: " + id));
        deleteDocument(document);
    }

    public void deleteDocument(Document document) {
        String publicId = buildCloudinaryPublicId(document);
        try {
            // resource_type must match (image, video, or raw)
            // 'auto' is not supported for destroy, usually stored in DB or inferred
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
            documentRepository.delete(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete from Cloudinary", e);
        }
    }

    // --- Helper Methods ---

    private String generateSubDirectory() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%d/%02d/%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
    }

    private String buildFolderPath(String baseDir, String subDir) {
        return (baseDir + "/" + subDir).replaceAll("^/+|/+$", "");
    }

    private String buildCloudinaryPublicId(Document doc) {
        // Cloudinary stores the path as part of the public_id
        return buildFolderPath(doc.getBaseDirectory(), doc.getSubDirectory()) + "/" + getFileNameWithoutExtension(doc.getFileName());
    }

    private String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return fileName;
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private Document saveDocumentMetadata(Map uploadResult, DocumentType type, EntityName entity, Long id) {
        Document document = new Document();
        document.setFileName(uploadResult.get("original_filename").toString() + "." + uploadResult.get("format").toString());

        // We split the public_id back to base/sub directories to keep your DB schema compatible
        String fullPublicId = uploadResult.get("public_id").toString();
        int lastSlash = fullPublicId.lastIndexOf('/');

        document.setBaseDirectory(type.getDirectory());
        document.setSubDirectory(fullPublicId.substring(type.getDirectory().length(), lastSlash).replaceAll("^/+", ""));

        document.setContentType(uploadResult.get("resource_type").toString());
        document.setUploadedForEntity(entity);
        document.setUploadedForId(id);
        document.setDocumentType(type);

        return documentRepository.save(document);
    }
}