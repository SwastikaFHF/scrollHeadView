package com.aitangba.testproject.html;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XBeats on 2019/12/10
 */
public class HtmlHelper {

    private static final String HOCK_ELEMENT = "hock";
    private static final String HOCK_MARKER = "<hock/>";
    private static final String HOCK_SUFFIX = "_hock";

    public static Spanned fromHtml(String source, List<Element> elements) {
        return Html.fromHtml(HOCK_MARKER + source, null, new CustomHandler(elements));
    }

    public interface Element {

        String getOriginElement();

        void onCreate(Attributes attrs);

        void handleTag(boolean open, String tag, Editable output);
    }


    private static class CustomHandler implements Html.TagHandler {

        private CustomContentHandler mContentHandler;
        private List<Element> mElements = new LinkedList<>();

        private CustomHandler(List<Element> elements) {
            mElements.addAll(elements);
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (mContentHandler == null) {
                mContentHandler = new CustomContentHandler(this, xmlReader.getContentHandler());
                xmlReader.setContentHandler(mContentHandler);
            }

            tag = tag.replace(HOCK_SUFFIX, "");
            for (Element element : mElements) {
                if (TextUtils.equals(element.getOriginElement(), tag)) {
                    element.handleTag(opening, tag, output);
                    break;
                }
            }
        }
    }

    private static class CustomContentHandler implements ContentHandler {

        private ContentHandler mInnerContentHandler;
        private CustomHandler mCustomHandler;

        CustomContentHandler(CustomHandler customHandler, ContentHandler innerContentHandler) {
            mCustomHandler = customHandler;
            mInnerContentHandler = innerContentHandler;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            mInnerContentHandler.setDocumentLocator(locator);
        }

        @Override
        public void startDocument() throws SAXException {
            mInnerContentHandler.startDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            mInnerContentHandler.endDocument();
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            mInnerContentHandler.startPrefixMapping(prefix, uri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            mInnerContentHandler.endPrefixMapping(prefix);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if (localName.equalsIgnoreCase(HOCK_ELEMENT)) {
                return;
            }
            for (Element element : mCustomHandler.mElements) {
                if (TextUtils.equals(element.getOriginElement(), localName)) {
                    element.onCreate(atts);
                    localName = localName + HOCK_SUFFIX;
                    qName = qName + HOCK_SUFFIX;
                    break;
                }
            }
            mInnerContentHandler.startElement(uri, localName, qName, atts);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equalsIgnoreCase(HOCK_ELEMENT)) {
                return;
            }

            for (Element element : mCustomHandler.mElements) {
                if (TextUtils.equals(element.getOriginElement(), localName)) {
                    localName = localName + HOCK_SUFFIX;
                    qName = qName + HOCK_SUFFIX;
                    break;
                }
            }
            mInnerContentHandler.endElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            mInnerContentHandler.characters(ch, start, length);
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            mInnerContentHandler.ignorableWhitespace(ch, start, length);
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            mInnerContentHandler.processingInstruction(target, data);
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
            mInnerContentHandler.skippedEntity(name);
        }
    }

}
