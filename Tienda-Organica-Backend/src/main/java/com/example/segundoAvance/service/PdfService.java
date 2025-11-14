package com.example.segundoAvance.service;

// 1. AÑADIMOS TODAS LAS IMPORTACIONES NECESARIAS
import com.example.segundoAvance.dto.ItemDTO; // (Aunque ya no la usamos aquí, la dejamos por si acaso)
import com.example.segundoAvance.model.DetallePedido; // <-- ¡Importante!
import com.example.segundoAvance.model.Pedido;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color; // <-- Importa Color
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal; // <-- ¡Importante!
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream; // <-- Importa Stream

@Service
public class PdfService {

    public ByteArrayInputStream generarComprobantePdf(Pedido pedido) throws IOException, DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // --- TÍTULO ---
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
        String tituloComprobante = pedido.getTipoComprobante().equalsIgnoreCase("factura") ? "FACTURA ELECTRÓNICA" : "BOLETA DE VENTA ELECTRÓNICA";
        Paragraph titulo = new Paragraph(tituloComprobante, fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // --- DATOS DE LA EMPRESA Y PEDIDO ---
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[] { 1f, 1f });
        infoTable.setSpacingAfter(20);

        // Columna Izquierda (Empresa)
        PdfPCell cellEmpresa = new PdfPCell();
        cellEmpresa.setBorder(Rectangle.NO_BORDER);
        cellEmpresa.addElement(new Paragraph("Allin Runa S.A.C."));
        cellEmpresa.addElement(new Paragraph("RUC: 20123456789"));
        cellEmpresa.addElement(new Paragraph("Av. Primavera 123, Lima, Perú"));
        cellEmpresa.addElement(new Paragraph("hola@allinruna.com"));
        infoTable.addCell(cellEmpresa);

        // Columna Derecha (Pedido)
        PdfPCell cellPedido = new PdfPCell();
        cellPedido.setBorder(Rectangle.NO_BORDER);
        cellPedido.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellPedido.addElement(new Paragraph("Pedido N°: " + pedido.getId()));
        cellPedido.addElement(new Paragraph("Fecha: " + pedido.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        infoTable.addCell(cellPedido);

        document.add(infoTable);

        // --- DATOS DEL CLIENTE ---
        document.add(new Paragraph("DATOS DEL CLIENTE:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        document.add(new Paragraph("Nombre: " + pedido.getNombre() + " " + pedido.getApellidos()));
        document.add(new Paragraph("Email: " + pedido.getEmail()));
        document.add(new Paragraph("Teléfono: " + pedido.getTelefono()));
        document.add(new Paragraph("Dirección: " + pedido.getDireccion() + ", " + pedido.getDistrito()));
        
        if (pedido.getTipoComprobante().equalsIgnoreCase("factura")) {
            document.add(new Paragraph("RUC: " + pedido.getRuc()));
            document.add(new Paragraph("Razón Social: " + pedido.getRazonSocial()));
        } else {
            document.add(new Paragraph("DNI: " + pedido.getDni()));
        }
        document.add(new Paragraph(" ")); // Espacio

        // --- TABLA DE DETALLES DEL PEDIDO ---
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3f, 1f, 1.5f, 1.5f });
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        // Encabezados de la tabla
        Stream.of("Producto", "Cant.", "P. Unit.", "Subtotal")
                .forEach(headerTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new Color(230, 230, 230)); // Gris claro
                    header.setBorderWidth(1);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                    table.addCell(header);
                });

        // --- ARREGLO 1: Iterar sobre las entidades, no sobre DTOs ---
        // Ya no necesitamos 'ObjectMapper' ni 'ItemDTO' aquí.
        // Usamos la lista de detalles que ya está en el objeto 'pedido'.
        
        // (Líneas eliminadas: ObjectMapper y conversión a List<ItemDTO>)

        for (DetallePedido item : pedido.getDetalles()) { // <-- ARREGLO 1
            table.addCell(item.getProducto().getNombre());
            table.addCell(String.valueOf(item.getCantidad()));
            table.addCell(String.format("S/ %.2f", item.getPrecioUnitario()));

            // --- ARREGLO 2: Usar .multiply() para BigDecimal ---
            BigDecimal subtotalItem = item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad()));
            table.addCell(String.format("S/ %.2f", subtotalItem));
        }

        document.add(table);

        // --- TOTALES ---
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(40);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setWidths(new float[] { 1f, 1f });

        // Función helper para añadir celdas de totales
        addTotalRow(totalTable, "Subtotal:", String.format("S/ %.2f", pedido.getSubtotal()));
        addTotalRow(totalTable, "Envío:", String.format("S/ %.2f", pedido.getCostoEnvio()));
        addTotalRow(totalTable, "Total a Pagar:", String.format("S/ %.2f", pedido.getTotal()), true);

        document.add(totalTable);

        // --- Pie de página ---
        Paragraph footer = new Paragraph("\nGracias por tu compra en Allin Runa", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // Método helper para añadir filas a la tabla de totales
    private void addTotalRow(PdfPTable table, String label, String value) {
        addTotalRow(table, label, value, false);
    }

    private void addTotalRow(PdfPTable table, String label, String value, boolean isBold) {
        Font font = isBold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12) : FontFactory.getFont(FontFactory.HELVETICA, 12);
        
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, font));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellValue);
    }
}