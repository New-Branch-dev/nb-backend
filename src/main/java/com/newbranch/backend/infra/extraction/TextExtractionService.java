package com.newbranch.backend.infra.extraction;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Slf4j
@Service
public class TextExtractionService {

    // 지원 포맷: PDF, PPTX, DOCX (구형 PPT/DOC은 poi-scratchpad 별도 추가 필요)
    public String extract(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) return null;

        try {
            String lower = name.toLowerCase();
            if (lower.endsWith(".pdf")) return extractFromPdf(file);
            if (lower.endsWith(".pptx")) return extractFromPptx(file);
            if (lower.endsWith(".docx")) return extractFromDocx(file);
            log.info("텍스트 추출 미지원 포맷: {}", name);
        } catch (Exception e) {
            log.warn("텍스트 추출 실패 [{}]: {}", name, e.getMessage());
        }
        return null;
    }

    private String extractFromPdf(MultipartFile file) throws Exception {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(doc);
        }
    }

    private String extractFromPptx(MultipartFile file) throws Exception {
        try (XMLSlideShow ppt = new XMLSlideShow(file.getInputStream())) {
            return ppt.getSlides().stream()
                    .flatMap(slide -> slide.getShapes().stream())
                    .filter(shape -> shape instanceof XSLFTextShape)
                    .map(shape -> ((XSLFTextShape) shape).getText())
                    .collect(Collectors.joining("\n"));
        }
    }

    private String extractFromDocx(MultipartFile file) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            return new XWPFWordExtractor(doc).getText();
        }
    }
}
