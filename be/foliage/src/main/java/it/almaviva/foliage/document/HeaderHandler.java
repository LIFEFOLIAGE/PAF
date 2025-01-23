package it.almaviva.foliage.document;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;

public class HeaderHandler implements IEventHandler{
	private IBlockElement renderedElement;
	//private Rectangle rect;
	private Document doc;
	private float tableHeight;
	private int pageCount;
	private boolean isBozza;


	public HeaderHandler(Document doc, IBlockElement renderedElement, int pageCount, boolean isBozza) {
		this.renderedElement = renderedElement;
		this.doc = doc;
		this.pageCount = pageCount;
		this.isBozza = isBozza;

		IRenderer renderer = renderedElement.createRendererSubTree();
		renderer.setParent(new DocumentRenderer(doc));

		// Simulate the positioning of the renderer to find out how much space the header table will occupy.
		LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(0, PageSize.A4)));
		tableHeight = result.getOccupiedArea().getBBox().getHeight();
	}

	@Override
	public void handleEvent(Event event) {
		PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
		PdfPage page = docEvent.getPage();
		PdfDocument pdfDoc = docEvent.getDocument();
		int pageNum = pdfDoc.getPageNumber(page);
		PdfCanvas canvas = new PdfCanvas(
			page.newContentStreamBefore(),
			page.getResources(),
			pdfDoc
		);

		PageSize pageSize = (PageSize) page.getPageSizeWithRotation();
		
		float coordX = pageSize.getX() + doc.getLeftMargin();
		float coordY = pageSize.getTop() - (doc.getTopMargin() - 20);
		float width = pageSize.getWidth() - doc.getRightMargin() - doc.getLeftMargin();
		float height = tableHeight;
		Rectangle rect = new Rectangle(coordX, coordY, width, height);

		Canvas can = new Canvas(
			canvas,
			rect
		);
		can.add(renderedElement);
		can.close();

		PdfCanvas canvasBot = new PdfCanvas(
				page.newContentStreamBefore(), 
				page.getResources(),
				pdfDoc
			);


		
		Paragraph p = new Paragraph()
			.setFontSize(10)
			.setTextAlignment(TextAlignment.RIGHT);
		p.add(
			String.format("%d / %d", pageNum, pageCount)
		);
		Canvas canBot  = new Canvas(
			canvasBot,
			new Rectangle(
				36,
				20,
				pageSize.getWidth() - 72,
				50
			)
		);
		canBot.add(p);
		canBot.close();

		if (isBozza)
		{
			float verticalOffset = 0;
			Text text = new Text("BOZZA BOZZA BOZZA BOZZA BOZZA BOZZA ");
			//text.setFont(font);
			text.setFontSize(56);
			text.setOpacity(0.125f);
			Paragraph paragraph = new Paragraph(text);
			//PageSize pageSize = (PageSize) page.getPageSizeWithRotation();
			float x = (pageSize.getLeft() + pageSize.getRight()) / 2;
			float y = (pageSize.getTop() + pageSize.getBottom()) / 2;
			float xOffset = 37f;//100f / 2;
			float rotationInRadians = (float) (Math.PI / 180 * 55f);
			doc.showTextAligned(paragraph, x - xOffset, y + verticalOffset, pageNum, TextAlignment.CENTER, VerticalAlignment.TOP, rotationInRadians);
		}
	}
	
}
