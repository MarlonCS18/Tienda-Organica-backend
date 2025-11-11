package com.example.segundoAvance.service;

import com.example.segundoAvance.model.Pedido;
import com.example.segundoAvance.model.Producto;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public ByteArrayInputStream generarBoletaPdf(Pedido pedido) throws IOException, DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // --- FUENTES ---
        Font FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
        Font FONT_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
        Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font FONT_HEADER_TABLA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        
        // ***** CAMBIO APLICADO AQUÍ: Tamaño de la fuente aumentado de 16 a 24 *****
        Font FONT_MARCA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new Color(0, 123, 255)); 

        // --- ENCABEZADO ---
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[] { 1f, 1f });
        headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        Paragraph marca = new Paragraph("TiendaTech", FONT_MARCA);
        PdfPCell marcaCell = new PdfPCell(marca);
        marcaCell.setBorder(Rectangle.NO_BORDER);
        marcaCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(marcaCell);

        PdfPCell textCell = new PdfPCell();
        Paragraph tituloBoleta = new Paragraph("Boleta de Venta", FONT_TITULO);
        tituloBoleta.setAlignment(Element.ALIGN_RIGHT);
        Paragraph numeroPedido = new Paragraph("Pedido N°: " + pedido.getId(), FONT_SUBTITULO);
        numeroPedido.setAlignment(Element.ALIGN_RIGHT);
        
        textCell.addElement(tituloBoleta);
        textCell.addElement(numeroPedido);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(textCell);

        document.add(headerTable);
        document.add(Chunk.NEWLINE);

        // --- El resto del documento se mantiene igual ---
        
        document.add(new Paragraph("Datos del Cliente", FONT_SUBTITULO));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------"));
        document.add(new Paragraph("Nombre: " + pedido.getUsuario().getNombreCompleto(), FONT_BODY));
        document.add(new Paragraph("Email: " + pedido.getUsuario().getEmail(), FONT_BODY));
        document.add(new Paragraph("Teléfono: " + pedido.getUsuario().getTelefono(), FONT_BODY));
        document.add(new Paragraph("Dirección de Envío: " + pedido.getUsuario().getDireccion(), FONT_BODY));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        document.add(new Paragraph("Fecha de Compra: " + pedido.getFechaCreacion().format(formatter), FONT_BODY));
        document.add(Chunk.NEWLINE);

        document.add(new Paragraph("Detalle del Pedido", FONT_SUBTITULO));
        document.add(Chunk.NEWLINE);
        PdfPTable tablaProductos = new PdfPTable(4);
        tablaProductos.setWidthPercentage(100);
        tablaProductos.setWidths(new float[] { 4f, 1.5f, 1f, 1.5f });
        tablaProductos.addCell(crearCeldaHeader("Producto", FONT_HEADER_TABLA));
        tablaProductos.addCell(crearCeldaHeader("Precio Unit.", FONT_HEADER_TABLA));
        tablaProductos.addCell(crearCeldaHeader("Cant.", FONT_HEADER_TABLA));
        tablaProductos.addCell(crearCeldaHeader("Subtotal", FONT_HEADER_TABLA));

        for (Producto producto : pedido.getProductos()) {
            int cantidad = 1; 
            double subtotal = producto.getPrecio() * cantidad;
            tablaProductos.addCell(crearCeldaBody(producto.getNombre(), FONT_BODY, Element.ALIGN_LEFT));
            tablaProductos.addCell(crearCeldaBody(String.format("S/ %.2f", producto.getPrecio()), FONT_BODY, Element.ALIGN_CENTER));
            tablaProductos.addCell(crearCeldaBody(String.valueOf(cantidad), FONT_BODY, Element.ALIGN_CENTER));
            tablaProductos.addCell(crearCeldaBody(String.format("S/ %.2f", subtotal), FONT_BODY, Element.ALIGN_RIGHT));
        }
        document.add(tablaProductos);

        Paragraph total = new Paragraph(String.format("TOTAL: S/ %.2f", pedido.getTotal()), FONT_TITULO);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(Chunk.NEWLINE);
        document.add(total);

        Paragraph footer = new Paragraph("¡Gracias por tu compra en TiendaTech!", FONT_SUBTITULO);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
    
    private PdfPCell crearCeldaHeader(String contenido, Font fuente) {
        PdfPCell cell = new PdfPCell(new Paragraph(contenido, fuente));
        cell.setBackgroundColor(new Color(33, 37, 41));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }
    
    private PdfPCell crearCeldaBody(String contenido, Font fuente, int alineacion) {
        PdfPCell cell = new PdfPCell(new Paragraph(contenido, fuente));
        cell.setHorizontalAlignment(alineacion);
        cell.setPadding(5);
        return cell;
    }
}