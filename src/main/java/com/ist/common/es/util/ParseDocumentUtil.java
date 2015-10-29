package com.ist.common.es.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;


public class ParseDocumentUtil {
    
    private static final Logger logger = Logger
            .getLogger(ParseDocumentUtil.class);
    
    public static String getText(String path) {
        if (path.indexOf(".") != -1) {
            int index = path.indexOf(".");
            String suffix = path.substring(index + 1);
            String text = "";
            if (suffix.equals("xls")) {
                text = readExcel(path);
            } else if (suffix.equals("xlsx")) {
                text = readExcelx(path);
            } else if (suffix.equals("doc")) {
                text = readDoc(path);
            } else if (suffix.equals("docx")) {
                text = readDocx(path);
            } else if (suffix.equals("ppt")) {
                text = readPpt(path);
            } else if (suffix.equals("pptx")) {
                text = readPptx(path);
            } else if (suffix.equals("pdf")) {
                text = readPdf(path);
            } else if (suffix.equals("txt")) {
                text = readTxt(path);
            }
            return text;
        }
        return "";
    }

    private static String readExcel(String xls) {
        // 创建输入流读取xls文件
        InputStream in;
        String text = "";
        try {
            in = new FileInputStream(xls);
            HSSFWorkbook workbook = new HSSFWorkbook(in); // 读取一个文件
            ExcelExtractor extractor = new ExcelExtractor(workbook);

            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            text = extractor.getText(); // Retrieves the text contents of the
                                        // file
        } catch (OfficeXmlFileException e) {
            readExcelx(xls);
        }  catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.info("FileNotFoundException");
        }// xls文件存储地址
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return text;

    }

    private static String readDoc(String doc) {
        // 创建输入流读取DOC文件
        FileInputStream in;
        String text = "";
        try {
            in = new FileInputStream(doc);
            WordExtractor extractor = null; // 创建WordExtractor
            extractor = new WordExtractor(in);// 对DOC文件进行提取

            text = extractor.getText();
        } catch (OfficeXmlFileException e) {
            readDocx(doc);
        }  catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.info("FileNotFoundException");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return text;

    }

    private static String readDocx(String docx) {

        XWPFWordExtractor extractor;
        String text = "";
        try {
            extractor = new XWPFWordExtractor(POIXMLDocument.openPackage(docx));
            text = extractor.getText();
        }catch (InvalidOperationException e) {
            e.printStackTrace();
        } catch (XmlException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return text;

    }

    private static String readPptx(String pptx) {
        XSLFPowerPointExtractor extractor;
        String text = "";
        try {
            extractor = new XSLFPowerPointExtractor(OPCPackage.open(pptx));
            text = extractor.getText();
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        } catch (XmlException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return text;
    }

    private static String readPpt(String ppt) {
        // 创建输入流读取ppt文件
        FileInputStream is;
        String text = "";
        try {
            is = new FileInputStream(ppt);

            SlideShow ss = new SlideShow(new HSLFSlideShow(is));// is
                                                                // 为文件的InputStream，建立SlideShow
            Slide[] slides = ss.getSlides();// 获得每一张幻灯片

            text = new String();
            for (int i = 0; i < slides.length; i++) {
                TextRun[] t = slides[i].getTextRuns();// 为了取得幻灯片的文字内容，建立TextRun
                for (int j = 0; j < t.length; j++) {
                    // System.out.println(t[j].getText());//这里会将文字内容加到content中去
                    text += t[j].getText();
                }
            }
        }catch (OfficeXmlFileException e) {
            readPptx(ppt);
        }  catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.info("FileNotFoundException");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }

    private static String readPdf(String pdf) {
        // 创建输入流读取pdf文件
        String text = "";
        FileInputStream is = null;
        PDDocument document = null;
        try {
            is = new FileInputStream(pdf);
            PDFParser parser = new PDFParser(is);
            parser.parse();
            document = parser.getPDDocument();
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            logger.info("FileNotFoundException");
            // e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }

    private static String readTxt(String text) {
        BufferedReader reader = null;
        StringBuffer sb = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(text)));
            String line = "";
            sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            logger.info("FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return sb.toString();
    }

    private static String readExcelx(String xlsx) {
        XSSFExcelExtractor extractor;
        String text = "";
        try {
            extractor = new XSSFExcelExtractor(POIXMLDocument.openPackage(xlsx));
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            text = extractor.getText();
        }catch (InvalidOperationException e) {
            e.printStackTrace();
        } catch (XmlException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }
}
