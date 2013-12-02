package gr.abiss.calipso.wicket;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class JsTextContainer extends WebMarkupContainer {

	private static final long serialVersionUID = 1283323791609913607L;
	private PageParameters parameters;

	public JsTextContainer(String id, PageParameters parameters) {
		super(id);
		this.parameters = parameters;
	}

	@Override
	public void onComponentTagBody(MarkupStream markupStream,
			ComponentTag openTag) {
		String tagBody = // new StringBuffer().append(
		ItemTemplateViewPage.getPackagedStyleString(this.parameters);// ).toString();
		replaceComponentTagBody(markupStream, openTag, tagBody);
	}

}