package itu.dd.server.test;

import static org.easymock.classextension.EasyMock.*;

import java.util.LinkedList;

import junit.framework.Assert;
import itu.dd.server.domain.GridCell;
import itu.dd.server.util.xstreem.converters.MoodMapConverter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

/**
 * JUnit test class for testing creating a moodMap, using a mock-object database connection.
 */
public class MoodMapTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link MoodMapConverter#marshal(Object,HierarchicalStreamWriter,MarshallingContext)}.
	 */
	@Test
	public void testMarshal() {
		XStream xStream = new XStream();
		xStream.registerConverter(new MoodMapConverter());
		xStream.alias("p",GridCell.class);
	
		LinkedList<?> gc = (LinkedList<?>)xStream.fromXML("<p value=\"120\" long=\"14.4889622588559\" lat=\"35.9237275622272\" />");
		GridCell tgc = new GridCell(14.4889622588559, 35.9237275622272);
		tgc.addValue(120);
		for(int i = 0;i < gc.size();i++) {
			GridCell cgc = (GridCell)gc.get(i); 
			Assert.assertEquals(cgc.getAverage(), tgc.getAverage());
			Assert.assertEquals(cgc.getLatitude(), tgc.getLatitude(), 0.0);
			Assert.assertEquals(cgc.getLongitude(), tgc.getLongitude(), 0.0);
		}
	}
	/**
	 * Test method for {@link MoodMapConverter#unmarshal(HierarchicalStreamReader,UnmarshallingContext)}.
	 */
	@Test
	public void testUnmarshal() {
		GridCell cc = createMock(GridCell.class);
		expect(cc.getLatitude()).andStubReturn(35.9237275622272);
		expect(cc.getLongitude()).andStubReturn(14.4889622588559);
		expect(cc.getAverage()).andStubReturn(120L);
		replay(cc);

		XStream xStream = new XStream();
		xStream.registerConverter(new MoodMapConverter());
		xStream.alias("p", cc.getClass());

		String xmlOutput = xStream.toXML(cc);
		//xmlOutput = xmlOutput.replaceAll("\n", "").replaceAll(" ", "");
		Assert.assertEquals("<p value=\"120\" long=\"14.4889622588559\" lat=\"35.9237275622272\"/>",xmlOutput);
		
	}


	/**
	 * Test method for {@link MoodMapConverter#canConvert(Class)}.
	 */
	@Test
	public void testCanConvert() {
		MoodMapConverter mmc = new MoodMapConverter();
		
		GridCell cc = createMock(GridCell.class);
		expect(cc.getLatitude()).andStubReturn(35.9237275622272);
		expect(cc.getLongitude()).andStubReturn(14.4889622588559);
		expect(cc.getAverage()).andStubReturn(120L);
		replay(cc);

		Assert.assertEquals(true,mmc.canConvert(GridCell.class));
	}
	
}
