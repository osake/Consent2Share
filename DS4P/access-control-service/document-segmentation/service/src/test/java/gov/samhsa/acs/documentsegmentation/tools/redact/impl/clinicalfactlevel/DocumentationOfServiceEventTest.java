package gov.samhsa.acs.documentsegmentation.tools.redact.impl.clinicalfactlevel;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import gov.samhsa.acs.brms.domain.ClinicalFact;
import gov.samhsa.acs.brms.domain.FactModel;
import gov.samhsa.acs.brms.domain.RuleExecutionContainer;
import gov.samhsa.acs.common.tool.DocumentAccessor;
import gov.samhsa.acs.common.tool.DocumentAccessorImpl;
import gov.samhsa.acs.common.tool.DocumentXmlConverter;
import gov.samhsa.acs.common.tool.DocumentXmlConverterImpl;
import gov.samhsa.acs.common.tool.FileReader;
import gov.samhsa.acs.common.tool.FileReaderImpl;
import gov.samhsa.acs.common.tool.SimpleMarshaller;
import gov.samhsa.acs.common.tool.SimpleMarshallerImpl;
import gov.samhsa.acs.common.tool.exception.SimpleMarshallerException;
import gov.samhsa.acs.documentsegmentation.tools.EmbeddedClinicalDocumentExtractor;
import gov.samhsa.acs.documentsegmentation.tools.EmbeddedClinicalDocumentExtractorImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@RunWith(MockitoJUnitRunner.class)
public class DocumentationOfServiceEventTest {
	
	public static final String TEST_PATH = "sampleC32-redactionHandlers/";
	public static final String FACTMODEL_PATH = "factmodel/";
	public static final String RULEEXECUTIONCONTAINER_PATH = "ruleexecutioncontainer/";

	private FileReader fileReader;
	private SimpleMarshaller marshaller;
	private DocumentAccessor documentAccessor;
	private DocumentXmlConverter documentXmlConverter;
	private EmbeddedClinicalDocumentExtractor embeddedClinicalDocumentExtractor;
	
	private DocumentationOfServiceEvent sut;

	@Before
	public void setUp() throws Exception {
		fileReader = new FileReaderImpl();
		marshaller = new SimpleMarshallerImpl();
		documentAccessor = new DocumentAccessorImpl();
		documentXmlConverter = new DocumentXmlConverterImpl();
		embeddedClinicalDocumentExtractor = new EmbeddedClinicalDocumentExtractorImpl(documentXmlConverter, documentAccessor);
		sut = new DocumentationOfServiceEvent(documentAccessor);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecute() throws IOException, SimpleMarshallerException, XPathExpressionException {
		// Arrange
		String c32FileName = "c32.xml";
		String factmodelXml = fileReader.readFile(TEST_PATH + FACTMODEL_PATH + c32FileName);
		String c32 = embeddedClinicalDocumentExtractor.extractClinicalDocumentFromFactModel(factmodelXml);
		String ruleExecutionContainerXml = fileReader.readFile(TEST_PATH + RULEEXECUTIONCONTAINER_PATH + c32FileName);
		RuleExecutionContainer ruleExecutionContainer = marshaller.unmarshalFromXml(RuleExecutionContainer.class, ruleExecutionContainerXml);
		Document c32Document = documentXmlConverter.loadDocument(c32);
		Document factModelDocument = documentXmlConverter.loadDocument(factmodelXml);
		FactModel factModel = marshaller.unmarshalFromXml(FactModel.class, factmodelXml);
		List<Node> listOfNodes = new LinkedList<Node>();
		Set<String> redactSectionCodesAndGeneratedEntryIds = new HashSet<String>();
		Set<String> redactSensitiveCategoryCodes = new HashSet<String>();
		ClinicalFact fact = factModel.getClinicalFactList().get(1);
		Set<String> valueSetCategories = new HashSet<String>();
		valueSetCategories.add("HIV");
		valueSetCategories.add("ETH");
		fact.setValueSetCategories(valueSetCategories);

		// Act
		sut.execute(c32Document, factModel.getXacmlResult(), factModel,
				factModelDocument, fact, ruleExecutionContainer, listOfNodes,
				redactSectionCodesAndGeneratedEntryIds,
				redactSensitiveCategoryCodes);
		
		// Assert
		assertEquals(1, listOfNodes.size());
		assertEquals(1, redactSectionCodesAndGeneratedEntryIds.size());
		assertEquals(1, redactSensitiveCategoryCodes.size());
		assertEquals(Node.ELEMENT_NODE, listOfNodes.get(0).getNodeType());
		assertEquals("serviceEvent", listOfNodes.get(0).getNodeName());
		assertEquals("d1e89", redactSectionCodesAndGeneratedEntryIds.toArray()[0]);
		assertEquals("ETH", redactSensitiveCategoryCodes.toArray()[0]);
	}
}
