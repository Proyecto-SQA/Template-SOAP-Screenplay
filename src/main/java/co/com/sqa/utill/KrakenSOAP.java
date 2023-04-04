package co.com.sqa.utill;

import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;

public class KrakenSOAP {


	
	public static void relajarValidacionesHTTPS() {
		SerenityRest.useRelaxedHTTPSValidation();
	}
	
	public static RequestSpecification obtenerRequest() {
		return request;
	}
	
	public static void configurarURL(String url) {
		request.baseUri(url);
	}
	
	public static void configurarPath(String path) {
		request.basePath(path);
	}

	// ----------------------UTILIDADES ADICIONALES--------------------------------


		/**
		 * El metodo inserta un valor en todas las ocurrencias de label dentro del
		 * request
		 * 
		 * @param request
		 *            String del body en formato XML
		 * @param label
		 *            etiqueta cuyo valor se desea reemplazar
		 * @param value
		 *            valor, valor que se le va a poner al label
		 * @return el request modificado con el nuevo valor para la etiqueta.
		 */

		public static SOAPHeader agregarHeader() {
			try {
				header = message.getSOAPHeader();
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return header;
		}

		public static void retirarHeader() {
			try {
				header = message.getSOAPHeader();
				header.detachNode();
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public static SOAPBody agregarCuerpo() {
			try {
				body = message.getSOAPBody();
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return body;
		}

		public static SOAPElement agregarElementoHijoAlElementoPadre(SOAPElement elementoSOAPPadre) {
			SOAPElement GetParamter = null;
			try {
				GetParamter = elementoSOAPPadre.addChildElement(new QName("http://examples/2001", "GetParamter"));
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return GetParamter;

		}

		public static <T> Node agregarModeloAlCuerpo(Class<T> object) {

			Node node = null;
			JAXBContext context;
			try {
				context = JAXBContext.newInstance(object.getClass());
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(object, node);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			body.appendChild(node);
			return node;
		}


		/**
		 * Metodo para comparar la estructura de una respuesta JSon y la estructura de
		 * una tabla convertida en un esquema JSon
		 * 
		 * @param pathEstructura
		 * @return
		 * @throws IOException
		 */
		/*public static Boolean comparacionDeEstructurasREST(String pathEstructura, String resultadoObtenido) {
			String estructura;
			try {
				estructura = new String(Files.readAllBytes(Paths.get(pathEstructura)));
				JSONObject jsonSubject = new JSONObject(resultadoObtenido);
				JSONObject jsonSchema = new JSONObject(estructura);
				Schema schema = SchemaLoader.load(jsonSchema);
				schema.validate(jsonSubject);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}*/


		// ----------------------UTILIDADES ADICIONALES--------------------------------

		/*
		 * public String setParametersSoap(String id, Table table, String requestModel)
		 * { String request = requestModel; ArrayList<String> paramNames =
		 * (ArrayList<String>) table.getColumnNames(); List<TableRow> rows =
		 * table.getTableRows();
		 * 
		 * if (rows.iterator().hasNext()) { TableRow row = rows.iterator().next(); for
		 * (String column : paramNames) { String value = row.getCell(column); if
		 * (!value.equals("?")) request = putValuesInSoapRequest(column, value,
		 * request); else request = commentOptionalParams(column, request); } } return
		 * request; }
		 */

		/**
		 * El metodo comenta la etiqueta <label> en un string en el request
		 * 
		 * @param request
		 *            body en formato XML
		 * @param label
		 *            etiqueta que se desea comentar
		 * 
		 *            Utiliza 3 pasos para validar el formato y hacer la modificacion.
		 *            1. Verifica si la etiqueta ya se encuentra entre marcas de
		 *            comentario. (<!-- -->) 2. Verifica si es el ultimo hijo que falta
		 *            por comentar en un nodo padre, en ese caso quita los comentarios
		 *            de los demas hijos y comenta el nodo padre. 3. Si llega a este
		 *            filtro, solo se comenta la etiqueta especificada.
		 */


		/**
		 * El metodo inserta un valor en la etiqueta "id" para los nodos de un body en
		 * formato REST cuyo valor en la etiqueta "type" corresponda con el valor dado.
		 * 
		 * @param request
		 *            String del body en formato JSON
		 * @param nodeType
		 *            valor de la etiqueta "type", cuyo valor "id" se desea reemplazar
		 * @param idValue
		 *            valor que se desea insertar en la etiqueta "id"
		 * @return el request modificado con el nuevo valor para la etiqueta id.
		 * 
		 *         //(\"type\"\s*:\s*\"individuals\"(\s|,)*\"id\"\s*:\s*\")([^\"]*)(\".*)
		 *         (\"id\"(\s*:\s*)\")([^\"]*)(\"(\s|,)*\"type\"\s*:\s*\"customer-accounts\".*)
		 * 
		 */

		static RequestSpecification request;

		public static void inicializarRequest() {
			request = SerenityRest.given();
		}

		public static void hacerPost() {
			request.post();
		}
		
		public static Response hacerGet(String endPoint) {
			return request.get(endPoint);
		}
		
		public static Response hacerPut(String endPoint) {
			return request.put(endPoint);
		}
		
		public static Response hacerPatch(String endPoint) {
			return request.patch(endPoint);
		}

		public static Response hacerPost(String endPoint) {
			return request.post(endPoint);
		}

		public static void configurarCuerpo(String body) {
			request.body(body);
		}
		
		public static void configurarCuerpo(Map<String, String> bodyData) {
			request.body(bodyData);
		}

		public static void validarValorNuloEnLlave(String resultKey) {
			assertNull(lastResponse().getBody().jsonPath().get(resultKey));
		}

		public static void validarValorNuloEnLlaveXML(String resultKey) {
			assertNull(lastResponse().getBody().xmlPath().get(resultKey));
		}

		public static void configurarHeaders(Map<String, String> headers) {
			request.headers(headers);
		}

		public static void compararStatus(int status) {
			assertThat(lastResponse().getStatusCode(), equalTo(status));
		}

		/**
		 * Metodo para comparar la estructura de una respuesta JSon y la estructura de
		 * una tabla convertida en un esquema JSon
		 * 
		 * @param pathEstructura
		 * @return
		 * @throws IOException
		 */
	/*	public static Boolean comparacionDeEstructurasREST(String pathEstructura, String resultadoObtenido) {
			String estructura;
			try {
				estructura = new String(Files.readAllBytes(Paths.get(pathEstructura)));
				JSONObject jsonSubject = new JSONObject(resultadoObtenido);
				JSONObject jsonSchema = new JSONObject(estructura);
				Schema schema = SchemaLoader.load(jsonSchema);
				schema.validate(jsonSubject);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}*/

		public static void compararLlave(String llave, String resultValue) {
			assertThat(lastResponse().getBody().jsonPath().get(llave).toString(), equalTo(resultValue));
		}

		public static void compararLlaveXML(String llave, String resultValue) {
			assertThat(lastResponse().getBody().xmlPath().get(llave).toString(), equalTo(resultValue));
		}
		// ---------------------------SOAP

		static MessageFactory messageFactory;
		static SOAPMessage message;
		static SOAPHeader header;
		static SOAPBody body;
		static SOAPEnvelope envelope;

		public static SOAPMessage crearMensajeSOAP() {
			try {
				messageFactory = MessageFactory.newInstance();
				message = messageFactory.createMessage();

			} catch (SOAPException e) {
				e.printStackTrace();
			}
			return message;
		}

		public static SOAPHeader generarHeaderSOAP() {
			try {
				header = message.getSOAPHeader();
			} catch (SOAPException e) {
				e.printStackTrace();
			}
			return header;
		}

		public static void retirarHeaderSOAP() {
			try {
				header = message.getSOAPHeader();
				header.detachNode();
			} catch (SOAPException e) {
				e.printStackTrace();
			}

		}

		public static SOAPBody generarCuerpoSOAP() {
			try {
				body = message.getSOAPBody();
			} catch (SOAPException e) {
				e.printStackTrace();
			}
			return body;
		}

		public static SOAPEnvelope generarEnvoltorioSOAP() {

			try {
				envelope = message.getSOAPPart().getEnvelope();
				envelope.removeAttribute("SOAP-ENV");
			} catch (SOAPException e) {
				e.printStackTrace();
			}

			return envelope;
		}

		public static void agregarDeclaracionDeEspacioDeNombre(SOAPElement soapElement, String prefijo, String URI) {
			try {
				soapElement.addNamespaceDeclaration(prefijo, URI);
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		}

		public static void agregarDeclaracionDeEspacioDeNombre(SOAPBody soapElement, String prefijo, String URI) {
			try {
				soapElement.addNamespaceDeclaration(prefijo, URI);
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		}

		public static void agregarDeclaracionDeEspacioDeNombre(SOAPHeader soapElement, String prefijo, String URI) {
			try {
				soapElement.addNamespaceDeclaration(prefijo, URI);
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		}

		public static void agregarDeclaracionDeEspacioDeNombre(SOAPEnvelope soapElement, String prefijo, String URI) {
			try {
				soapElement.addNamespaceDeclaration(prefijo, URI);
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		}

		public static SOAPElement agregarElementoSOAPAlCuerpo(String URI, String parteLocal, String prefijo) {
			QName bodyName = new QName(URI, parteLocal, prefijo);
			SOAPElement bodyElement = null;
			try {
				bodyElement = body.addBodyElement(bodyName);
			} catch (SOAPException e) {
				e.printStackTrace();
			}
			return bodyElement;
		}

		public static SOAPElement agregarElementoSOAPAlCuerpo(String URI, String parteLocal) {
			QName bodyName = new QName(URI, parteLocal);
			SOAPElement bodyElement = null;
			try {
				bodyElement = body.addBodyElement(bodyName);
			} catch (SOAPException e) {
				e.printStackTrace();
			}
			return bodyElement;
		}

		public static SOAPElement agregarElementoSOAPAlCuerpo(String parteLocal) {
			QName bodyName = new QName(parteLocal);
			SOAPElement bodyElement = null;
			try {
				bodyElement = body.addBodyElement(bodyName);
			} catch (SOAPException e) {
				e.printStackTrace();
			}
			return bodyElement;
		}

		public static SOAPElement agregarElementoHijoAlElementoPadre(SOAPElement elementoSOAPPadre, String URI,
				String parteLocal) {
			SOAPElement GetParamter = null;
			try {
				GetParamter = elementoSOAPPadre.addChildElement(new QName(URI, parteLocal));
			} catch (SOAPException e) {
				e.printStackTrace();
			}

			return GetParamter;

		}

		public static SOAPElement agregarElementoHijoAlElementoPadre(SOAPElement elementoSOAPPadre, String parteLocal) {
			SOAPElement GetParamter = null;
			try {
				GetParamter = elementoSOAPPadre.addChildElement(new QName(parteLocal));
			} catch (SOAPException e) {
				e.printStackTrace();
			}

			return GetParamter;

		}

		public static SOAPElement agregarTextoAlNodo(SOAPElement soapElement, String texto) {
			try {
				soapElement.addTextNode(texto);
			} catch (SOAPException e) {
				e.printStackTrace();
			}
			return soapElement;
		}

		public static void agregarDeclaracionDeDocumentoXML() {
			try {
				message.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		}

		public static SOAPEnvelope cambiarNombreDelEnvoltorio(SOAPEnvelope soapEnvelope, String nuevoNombre) {
			try {
				soapEnvelope.setElementQName(new QName(nuevoNombre));
			} catch (SOAPException e) {
				e.printStackTrace();
			}
			return soapEnvelope;
		}

		public static SOAPEnvelope cambiarPrefijoDelEnvoltorio(SOAPEnvelope soapEnvelope, String nuevoPrefijo) {
			soapEnvelope.setPrefix(nuevoPrefijo);
			return soapEnvelope;
		}

		public static SOAPBody cambiarPrefijoDelCuerpo(SOAPBody soapBody, String nuevoPrefijo) {
			soapBody.setPrefix(nuevoPrefijo);
			return soapBody;
		}

		public static void guardarCambios() {
			try {
				message.saveChanges();
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		}

		public static void imprimirMensajeSOAPEnConsola() {
			try {

				System.out.println(message.toString());
				message.writeTo(System.out);

			} catch (SOAPException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static String convertirMensajeSOAPEnString() {
			final StringWriter sw = new StringWriter();

			try {
				TransformerFactory.newInstance().newTransformer().transform(new DOMSource(message.getSOAPPart()),
						new StreamResult(sw));
			} catch (TransformerException e) {
				throw new RuntimeException(e);
			}
			return sw.toString();
		}

		public static SOAPEnvelope eliminarDeclaracionDeEspacioDeNombre(SOAPEnvelope soapEnvelope, String prefijo) {
			soapEnvelope.removeNamespaceDeclaration(prefijo);
			return soapEnvelope;
		}

		public static String removerEspaciosDeNombre(String xml, String nombreElemento) {

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;
			String modifiedRequestXML = "";
			try {
				transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.INDENT, "false");
				// System.out.println("before xml = " + xml);
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputSource inputSource = new InputSource(new StringReader(xml));
				Document xmlDoc = builder.parse(inputSource);
				Node root = xmlDoc.getDocumentElement();
				System.out.println(root.getNodeName());
				while (!root.getNodeName().equals(nombreElemento))

					root = root.getNextSibling();

				NodeList rootchildren = root.getChildNodes();
				Element newroot = xmlDoc.createElement(root.getNodeName());
				for (int i = 0; i < rootchildren.getLength(); i++) {
					newroot.appendChild(rootchildren.item(i).cloneNode(true));
				}
				xmlDoc.replaceChild(newroot, root);
				DOMSource requestXMLSource = new DOMSource(xmlDoc.getDocumentElement());
				StringWriter requestXMLStringWriter = new StringWriter();
				StreamResult requestXMLStreamResult = new StreamResult(requestXMLStringWriter);
				transformer.transform(requestXMLSource, requestXMLStreamResult);
				modifiedRequestXML = requestXMLStringWriter.toString();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return modifiedRequestXML;
		}

		// ----------------------UTILIDADES ADICIONALES--------------------------------

		public static boolean isNumeric(ArrayList<String> actualValues) {
			String regexFindLabel = "(\\d+\\.?\\d*)|null";
			Pattern p = Pattern.compile(regexFindLabel);
			Matcher m;
			for (String s : actualValues) {
				m = p.matcher(s);
				if (!m.matches()) {
					return false;
				}
			}
			return true;
		}

		public static boolean isText(ArrayList<String> actualValues) {
			String regexFindLabel = "null|\".*\"";
			Pattern p = Pattern.compile(regexFindLabel);
			Matcher m;
			for (String s : actualValues) {
				m = p.matcher(s);
				if (!m.matches()) {
					return false;
				}
			}
			return true;
		}

		/*
		 * public String setParametersSoap(String id, Table table, String requestModel)
		 * { String request = requestModel; ArrayList<String> paramNames =
		 * (ArrayList<String>) table.getColumnNames(); List<TableRow> rows =
		 * table.getTableRows();
		 * 
		 * if (rows.iterator().hasNext()) { TableRow row = rows.iterator().next(); for
		 * (String column : paramNames) { String value = row.getCell(column); if
		 * (!value.equals("?")) request = putValuesInSoapRequest(column, value,
		 * request); else request = commentOptionalParams(column, request); } } return
		 * request; }
		 */

		/**
		 * El metodo comenta la etiqueta <label> en un string en el request
		 * 
		 * @param request
		 *            body en formato XML
		 * @param label
		 *            etiqueta que se desea comentar
		 * 
		 *            Utiliza 3 pasos para validar el formato y hacer la modificacion.
		 *            1. Verifica si la etiqueta ya se encuentra entre marcas de
		 *            comentario. (<!-- -->) 2. Verifica si es el ultimo hijo que falta
		 *            por comentar en un nodo padre, en ese caso quita los comentarios
		 *            de los demas hijos y comenta el nodo padre. 3. Si llega a este
		 *            filtro, solo se comenta la etiqueta especificada.
		 */
		public static String commentOptionalParams(String label, String request) {
			String regExCommented = "(<!--[^-]*(<.*" + label + ">.*</.*" + label + ">)[^-]*-->)";
			String regexLastChildToComment = "<[^</>]+>\\s*" + "(<!--\\s*<.+>.*</.+>\\s-->\\s*)*" + "<.*" + label
					+ ">.*</.*" + label + ">\\s*" + "(<!--\\s*<.+>.*</.+>\\s*-->\\s*)" + "*\\s*</.+>";
			String regexSingleNode = "<[^<>]*" + label + ">.*<[^<>]*" + label + ">";
			Matcher matcher = Pattern.compile(regExCommented).matcher(request);
			if (!matcher.find()) {
				matcher = Pattern.compile(regexLastChildToComment, Pattern.MULTILINE).matcher(request);
				if (matcher.find()) {
					StringBuffer sb = new StringBuffer();
					String match = matcher.group(0);
					match = match.replaceAll("<!--", "");
					match = match.replaceAll("-->", "");
					match = "<!--" + match + "-->";
					matcher.appendReplacement(sb, match);
					matcher.appendTail(sb);
					request = sb.toString();
				} else {
					matcher = Pattern.compile(regexSingleNode).matcher(request);
					request = matcher.replaceFirst("<!-- $0 -->");
				}
			}
			return request;
		}

		/**
		 * El metodo inserta un valor en todas las ocurrencias de label dentro del
		 * request
		 * 
		 * @param request
		 *            String del body en formato XML
		 * @param label
		 *            etiqueta cuyo valor se desea reemplazar
		 * @param value
		 *            valor, valor que se le va a poner al label
		 * @return el request modificado con el nuevo valor para la etiqueta.
		 */
		public static String ponerValoresEnRequestSOAP(String label, String value, String request) {
			String regexFindLabel = "(<[^<>]*" + label + ">)([^<>]+)(</[^<>]*" + label + ">)";
			Matcher matcher = Pattern.compile(regexFindLabel).matcher(request);
			return matcher.replaceAll("$1" + value + "$3");
		}

		/**
		 * El metodo inserta un valor en todas las ocurrencias de label dentro del
		 * request
		 * 
		 * @param request
		 *            String del body en formato XML
		 * @param label
		 *            etiqueta cuyo valor se desea reemplazar
		 * @param value
		 *            valor, valor que se le va a poner al label
		 * @return el request modificado con el nuevo valor para la etiqueta.
		 */
		public static String ponerValoresEnRequestSOAP(List<List<String>> lista) {
			String request = leerCuerpoDeArchivo(lista.get(1).get(0));

			for (int i = 1; i < lista.get(0).size(); i++) {
				String regexFindLabel = "(<[^<>]*" + lista.get(0).get(i) + ">)([^<>]+)(</[^<>]*" + lista.get(0).get(i)
						+ ">)";
				Matcher matcher = Pattern.compile(regexFindLabel).matcher(request);
				request = matcher.replaceAll("$1" + lista.get(1).get(i) + "$3");
			}
			return request;
		}

		/**
		 * El metodo inserta un valor en todas las ocurrencias de label dentro del
		 * request
		 * 
		 * @param request
		 *            String del body en formato json
		 * @param label
		 *            etiqueta cuyo valor se desea reemplazar
		 * @param value
		 *            valor que se le va a poner al label
		 * @return el request modificado con el nuevo valor para la etiqueta.
		 */
		public String putValuesInRestRequest(String label, String value, String request) {

			String regexFindLabel = "(\"" + label + "\\\\?\":\\s*\\\\*\"?)([^\",{}\\\\]*)(\"?)";
			Matcher matcher = Pattern.compile(regexFindLabel).matcher(request);
			return matcher.replaceAll("$1" + value + "$3");
		}

		/**
		 * El metodo inserta un valor en la etiqueta "id" para los nodos de un body en
		 * formato REST cuyo valor en la etiqueta "type" corresponda con el valor dado.
		 * 
		 * @param request
		 *            String del body en formato JSON
		 * @param nodeType
		 *            valor de la etiqueta "type", cuyo valor "id" se desea reemplazar
		 * @param idValue
		 *            valor que se desea insertar en la etiqueta "id"
		 * @return el request modificado con el nuevo valor para la etiqueta id.
		 * 
		 *         //(\"type\"\s*:\s*\"individuals\"(\s|,)*\"id\"\s*:\s*\")([^\"]*)(\".*)
		 *         (\"id\"(\s*:\s*)\")([^\"]*)(\"(\s|,)*\"type\"\s*:\s*\"customer-accounts\".*)
		 * 
		 */
		public String putIdInRestRequestByType(String nodeType, String idValue, String request) {
			String modifiedRequest = request;
			String regexFindTypeLabelAfterId = "(\"id\"(\\s*:\\s*)\")([^\"]*)(\"(\\s|,)*\"type\"\\s*:\\s*\"" + nodeType
					+ "\".*)";
			Matcher matcher = Pattern.compile(regexFindTypeLabelAfterId).matcher(modifiedRequest);
			modifiedRequest = matcher.replaceAll("$1" + idValue + "$4");

			String regexFindTypeLabelBeforeId = "(\"type\"\\s*:\\s*\"" + nodeType
					+ "\"(\\s|,)*\"id\"\\s*:\\s*\")([^\"]*)(\".*)";
			matcher = Pattern.compile(regexFindTypeLabelBeforeId).matcher(modifiedRequest);
			modifiedRequest = matcher.replaceAll("$1" + idValue + "$4");
			return modifiedRequest;
		}

		public String setMessageSeq(String request) {
			String column = "MessageSeq";
			String value = (new java.text.SimpleDateFormat("yyyyMMddHHmmssS")).format(new Date());
			return ponerValoresEnRequestSOAP(column, value, request);
		}

		public String setRandomIdRestRequest(String request, String column, String prefix) {
			String value = prefix + (new java.text.SimpleDateFormat("yyyyMMddHHmmssS")).format(new Date());
			return putValuesInRestRequest(column, value, request);
		}

		/*
		 * public Table createParameterTable(String parameters) throws IOException {
		 * Reader reader = Files.newBufferedReader(Paths.get(parameters)); CSVParser
		 * parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
		 * ArrayList<String> headers = new ArrayList<>(parser.getHeaderMap().keySet());
		 * ArrayList<String> values = new ArrayList<>();
		 * 
		 * for (CSVRecord record : parser) {
		 * 
		 * for (String s : record) { values.add(s); }
		 * 
		 * }
		 * 
		 * Table params = new Table(headers); params.addRow(values); return params; }
		 */

		public HashMap<String, Object> cleanEmptyRestParams(HashMap<String, Object> serviceParams) {
			Iterator<Entry<String, Object>> entryIt = serviceParams.entrySet().iterator();
			while (entryIt.hasNext()) {
				String param = entryIt.next().getValue().toString();
				if (param.equals("") || param.contains("<"))
					entryIt.remove();
			}
			return serviceParams;
		}

		public static String leerCuerpoDeArchivo(String cuerpoXML) {
			try {
				return new String(Files.readAllBytes(Paths.get(cuerpoXML)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}

		public static void guardarUltimaRespuestaXML(String pathRespuesta) {
			try {
				FileUtils.writeStringToFile(new File(pathRespuesta), formatXML(lastResponse().asString()),
						Charset.forName("UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*public static void generarEstructuraSOAP(String pathEstructura, String pathRespuesta) {
			guardarUltimaRespuestaXML(pathRespuesta);
			File file = new File(pathRespuesta);

			try {
				FileUtils.writeStringToFile(new File(pathEstructura), formatXML(new XsdGen().parse(file).toString()),
						Charset.forName("UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}*/

		public static boolean comparacionDeEstructurasSOAP(String pathEstructura, String pathRespuesta) {
			File schemaFile = new File("src\\test\\resources\\com\\sqasa\\rest\\demo\\structures\\Esquema2.xsd");
			Source xmlFile = new StreamSource(
					new File("src\\test\\resources\\com\\sqasa\\rest\\demo\\responses\\Response.xml"));
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			javax.xml.validation.Schema schema;
			try {
				schema = schemaFactory.newSchema(schemaFile);
				Validator validator = schema.newValidator();
				validator.validate(xmlFile);

				System.out.println("VALIDO");
			} catch (SAXException e) {
				e.printStackTrace();
				Assert.fail();
			} catch (IOException e) {
				e.printStackTrace();
				Assert.fail();
			}

			return true;

		}

		public static String formatXML(String input) {
			
				Source xmlInput = new StreamSource(new StringReader(input));
				StringWriter stringWriter = new StringWriter();
				StreamResult xmlOutput = new StreamResult(stringWriter);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				// transformerFactory.setAttribute("indent-number", 2);
				Transformer transformer;
				try {
					transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					transformer.transform(xmlInput, xmlOutput);
				} catch (TransformerConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}
				return xmlOutput.getWriter().toString();
			
		}
		
}
