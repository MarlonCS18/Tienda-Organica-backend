package com.example.segundoAvance.service;

import com.example.segundoAvance.dto.ItemDTO;
import com.example.segundoAvance.model.Pedido;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    @Autowired
    private ObjectMapper objectMapper;

    public ByteArrayInputStream generarComprobantePdf(Pedido pedido) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- Título y Logo (Opcional) ---
            // Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            // Paragraph titulo = new Paragraph("Allin Runa", fontTitulo);
            // document.add(titulo);

            // --- Título del Comprobante ---
            Font fontComprobante = new Font(Font.HELVETICA, 16, Font.BOLD, Color.DARK_GRAY);
            String tipoComprobante = "factura".equals(pedido.getTipoComprobante()) ? "FACTURA ELECTRÓNICA" : "BOLETA DE VENTA ELECTRÓNICA";
            Paragraph pTipo = new Paragraph(tipoComprobante, fontComprobante);
            pTipo.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(pTipo);

            Paragraph pId = new Paragraph("Pedido N°: " + pedido.getId(), new Font(Font.HELVETICA, 12, Font.BOLD));
            pId.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(pId);
            
            document.add(new Paragraph(" ")); // Espacio

            // --- Datos del Cliente ---
            document.add(new Paragraph("Datos del Cliente:", new Font(Font.HELVETICA, 12, Font.BOLD)));

            if ("factura".equals(pedido.getTipoComprobante())) {
                document.add(new Paragraph("Razón Social: " + pedido.getRazonSocial()));
                document.add(new Paragraph("RUC: " + pedido.getRuc()));
            } else {
                document.add(new Paragraph("Cliente: " + pedido.getNombre() + " " + pedido.getApellidos()));
                document.add(new Paragraph("DNI: " + pedido.getDni()));
            }
            document.add(new Paragraph("Email: " + pedido.getEmail()));
            document.add(new Paragraph("Fecha de Emisión: " + pedido.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph(" "));

            // --- Dirección de Envío ---
            document.add(new Paragraph("Datos de Envío:", new Font(Font.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph("Dirección: " + pedido.getDireccion()));
            document.add(new Paragraph(pedido.getDistrito() + ", " + pedido.getProvincia() + ", " + pedido.getDepartamento()));
            if(pedido.getReferencia() != null && !pedido.getReferencia().isEmpty()) {
                document.add(new Paragraph("Referencia: " + pedido.getReferencia()));
            }
            document.add(new Paragraph(" "));

            // --- Tabla de Productos ---
            document.add(new Paragraph("Detalle del Pedido:", new Font(Font.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4); // 4 columnas
            table.setWidthPercentage(100);
            table.setWidths(new float[] {1f, 4f, 2f, 2f});

            // Encabezados de tabla
            Font headFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Color headerBg = new Color(52, 58, 64); // Dark gray
            
            PdfPCell hcell;
            hcell = new PdfPCell(new Paragraph("Cant.", headFont)); hcell.setBackgroundColor(headerBg); hcell.setBorder(Rectangle.NO_BORDER); table.addCell(hcell);
            hcell = new PdfPCell(new Paragraph("Producto", headFont)); hcell.setBackgroundColor(headerBg); hcell.setBorder(Rectangle.NO_BORDER); table.addCell(hcell);
            hcell = new PdfPCell(new Paragraph("P. Unit.", headFont)); hcell.setBackgroundColor(headerBg); hcell.setBorder(Rectangle.NO_BORDER); table.addCell(hcell);
            hcell = new PdfPCell(new Paragraph("Subtotal", headFont)); hcell.setBackgroundColor(headerBg); hcell.setBorder(Rectangle.NO_BORDER); table.addCell(hcell);

            // Decodificar el JSON de 'detalles'
            List<ItemDTO> items = objectMapper.readValue(pedido.getDetalles(), new TypeReference<List<ItemDTO>>() {});

            // Llenar tabla
            for (ItemDTO item : items) {
                // (Necesitaríamos buscar el nombre del producto, pero por ahora usamos el ID)
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell("Producto ID: " + item.getId()); // (Mejora: Buscar el nombre real)
                table.addCell(String.format("S/ %.2f", item.getPrecio()));
                table.addCell(String.format("S/ %.2f", item.getPrecio() * item.getQuantity()));
            }
            
            document.add(table);
            document.add(new Paragraph(" "));

            // --- Totales ---
            document.add(new Paragraph("Subtotal: S/ " + String.format("%.2f", pedido.getSubtotal()), new Font(Font.HELVETICA, 10)));
            document.add(new Paragraph("Envío: S/ " + String.format("%.2f", pedido.getCostoEnvio()), new Font(Font.HELVETICA, 10)));
            document.add(new Paragraph("Total Pagado: S/ " + String.format("%.2f", pedido.getTotal()), new Font(Font.HELVETICA, 14, Font.BOLD)));

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}