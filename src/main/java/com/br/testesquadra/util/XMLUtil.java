package com.br.testesquadra.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.br.testesquadra.util.exception.SystemException;


/**
 * Utility class for manipulating XML files using JAXB
 */
public class XMLUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(XMLUtil.class);

	private static final String W3C_XML_SCHEMA_INSTANCE_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";

	/** Default construct */
	protected XMLUtil() {
		// Utility Class
	}

	public static Object deserializarXml(final String xml, final URL schemaUrl, final String packageContext) {
		return XMLUtil.deserializarXml(xml, schemaUrl, XMLUtil.getContext(packageContext));
	}

	public static Object deserializarXml(final String xml, final URL schemaUrl, final Class<?>... classes) {
		return XMLUtil.deserializarXml(xml, schemaUrl, XMLUtil.getContext(classes));
	}

	public static Object deserializarXml(final String xml, final String packageContext) {
		return XMLUtil.deserializarXml(xml, null, XMLUtil.getContext(packageContext));
	}

	public static Object deserializarXml(final String xml, final Class<?>... classes) {
		return XMLUtil.deserializarXml(xml, null, XMLUtil.getContext(classes));
	}

	/**
	 * Unserializes an XML for their JAXB bean.
	 *
	 * @Param xml XML to be unserialized
	 * @param schemaUrl
	 *            Base URL of Schema
	 * @param JAXBContext
	 *            Context to be used
	 * @return Object
	 */
	public static Object deserializarXml(final String xml, final URL schemaUrl, final JAXBContext context) {
		Object object = null;

		try {

			XMLUtil.LOGGER.debug("Unserialized XML: {}", xml);

			final Unmarshaller unmarshaller = context.createUnmarshaller();

			// Only valid if you specify a schema
			if (schemaUrl != null) {
				final SchemaFactory sf = SchemaFactory.newInstance(XMLUtil.W3C_XML_SCHEMA_INSTANCE_NS_URI);

				final Schema schema = sf.newSchema(schemaUrl);
				unmarshaller.setSchema(schema);
			}

			object = unmarshaller.unmarshal(new StringReader(xml));

			XMLUtil.LOGGER.debug("XML unserialized com sucesso");

		} catch (final SAXException e) {
			throw new SystemException("Error validating XML file", e);
		} catch (final JAXBException e) {
			throw new SystemException("Error validating XML file", e);
		}

		return object;
	}

	/**
	 * Serializa um bean JAXB para uma string XML
	 *
	 * @param object
	 *            Bean JAXB a ser serializado
	 * @param packageContext
	 *            Pacote onde se encontram os demais beans JAXB
	 * @return String xml do bean JAXB serializado
	 */
	public static String serializarXml(final Object object, final String packageContext) {
		String xml = null;

		try {

			XMLUtil.LOGGER.debug("Serializando Objeto: {}", object);

			final JAXBContext context = JAXBContext.newInstance(packageContext);
			final Marshaller marshaller = context.createMarshaller();

			final StringWriter writer = new StringWriter();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(object, writer);

			xml = writer.toString();

		} catch (final JAXBException e) {
			throw new SystemException("Erro ao tentar serializar objeto", e);
		}

		return xml;
	}

	private static JAXBContext getContext(final String packageContext) {
		try {
			return JAXBContext.newInstance(packageContext);
		} catch (final JAXBException e) {
			throw new SystemException("Erro ao obter JAXBContext", e);
		}
	}

	private static JAXBContext getContext(final Class<?>... classes) {
		try {
			return JAXBContext.newInstance(classes);
		} catch (final JAXBException e) {
			throw new SystemException("Erro ao obter JAXBContext", e);
		}
	}

	public static String extrairTipoXML(final String xml) {
		return xml.trim().split(" ", 2)[0].replaceAll("<", "");
	}

}
