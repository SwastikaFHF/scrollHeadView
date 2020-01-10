package com.aitangba.testproject.html;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Html;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * Created by XBeats on 2019/12/10
 */
class HtmlHelper {
    private static final String HOCK_SUFFIX = "_hook";

    static class CustomHandler implements Html.TagHandler {

        private CustomContentHandler mContentHandler;
        private HashMap<String, ElementHandler> mElementsHandlerMap = new HashMap<>();
        private LinkedList<Element> mElementsMap = new LinkedList<>();

        CustomHandler(@NonNull Map<String, ElementHandler> elementsHandlerMap) {
            mElementsHandlerMap.putAll(elementsHandlerMap);
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (mContentHandler == null) {
                mContentHandler = new CustomContentHandler(this, xmlReader.getContentHandler());
                xmlReader.setContentHandler(mContentHandler);
            }

            final String originTag = tag.replace(HOCK_SUFFIX, "");
            ElementHandler elementHandler = mElementsHandlerMap.get(originTag);
            if (elementHandler != null) {
                if (opening) {
                    Element element;
                    for (int i = mElementsMap.size() - 1; i >= 0; i--) {
                        element = mElementsMap.get(i);
                        if (originTag.equals(element.tag) && element.startIndex == -1) {
                            element.startIndex = output.length();
                            break;
                        }
                    }
                } else {
                    Element element = null;
                    for (int i = mElementsMap.size() - 1; i >= 0; i--) {
                        if (originTag.equals(mElementsMap.get(i).tag)) {
                            element = mElementsMap.get(i);
                            break;
                        }
                    }
                    final int startIndex = element != null ? element.startIndex : output.length();
                    final Map<String, String> attrMap = element != null ? element.attrMap : new HashMap<>();
                    elementHandler.handleTag(startIndex, attrMap, output);
                    attrMap.clear();
                    mElementsMap.remove(element);
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
            if (mCustomHandler.mElementsHandlerMap.containsKey(localName) && mCustomHandler.mElementsHandlerMap.get(localName) != null) {
                Element element = new Element(localName);
                mCustomHandler.mElementsMap.add(element);
                Objects.requireNonNull(mCustomHandler.mElementsHandlerMap.get(localName)).onCreate(element.attrMap, atts);
                localName = localName + HOCK_SUFFIX;
                qName = qName + HOCK_SUFFIX;
            }
            mInnerContentHandler.startElement(uri, localName, qName, atts);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (mCustomHandler.mElementsHandlerMap.containsKey(localName) && mCustomHandler.mElementsHandlerMap.get(localName) != null) {
                localName = localName + HOCK_SUFFIX;
                qName = qName + HOCK_SUFFIX;
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

    private static class Element {
        private final String tag;
        private int startIndex = -1;
        private HashMap<String, String> attrMap = new HashMap<>();

        private Element(String tag) {
            this.tag = tag;
        }
    }
}
