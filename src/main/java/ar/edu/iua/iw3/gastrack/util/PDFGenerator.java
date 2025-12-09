package ar.edu.iua.iw3.gastrack.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import ar.edu.iua.iw3.gastrack.model.serializers.DTO.ConciliacionDTO;
/**
 * Generador de PDF para la conciliación de órdenes
 * @author Leandro Biondi
 * @author Antonella Badami
 * @author Benjamin Vargas
 * @since 09/12/2025
 */
public class PDFGenerator {
    public byte[] generarConciliacionPdf(ConciliacionDTO dto) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // ====== TÍTULO ======
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Reporte de Conciliación", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE);

            // ====== DATOS GENERALES (NUMERO ORDEN + CODIGO) ======
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(10f);

            infoTable.addCell(headerCell("Número de Orden"));
            infoTable.addCell(String.valueOf(dto.getNumeroOrden()));

            infoTable.addCell(headerCell("Código Externo"));
            infoTable.addCell(dto.getCodigoExterno() != null ? dto.getCodigoExterno() : "-");

            document.add(infoTable);

            // ====== TABLA DE DATOS TECNICOS ======
            PdfPTable table = new PdfPTable(2); // Campo / Valor
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            table.addCell(headerCell("Campo"));
            table.addCell(headerCell("Valor"));

            table.addCell("Pesaje Inicial");
            table.addCell(formatNumber(dto.getPesajeInicial()));

            table.addCell("Pesaje Final");
            table.addCell(formatNumber(dto.getPesajeFinal()));

            table.addCell("Producto Cargado");
            table.addCell(formatNumber(dto.getProductoCargado()));

            table.addCell("Neto Balanza");
            table.addCell(formatNumber(dto.getNetoBalanza()));

            table.addCell("Diferencia Balanza / Caudalímetro");
            table.addCell(formatNumber(dto.getDifBalanzaCaudalimentro()));

            table.addCell("Promedio Caudal");
            table.addCell(formatNumber(dto.getPromedioCaudal()));

            table.addCell("Promedio Temperatura");
            table.addCell(formatNumber(dto.getPromedioTemperatura()));

            table.addCell("Promedio Densidad");
            table.addCell(formatNumber(dto.getPromedioDensidad()));

            document.add(table);

            document.add(Chunk.NEWLINE);

            // ====== FIRMA / PIE ======
            Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC);
            Paragraph footer = new Paragraph(
                    "Documento generado automáticamente por el sistema",
                    footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de conciliación", e);
        }
    }

    private PdfPCell headerCell(String text) {
        Font font = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(230, 230, 230));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private String formatNumber(double value) {
        return String.format("%.2f", value);
    }
}
