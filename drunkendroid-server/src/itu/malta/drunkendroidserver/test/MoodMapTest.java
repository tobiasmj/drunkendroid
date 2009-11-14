package itu.malta.drunkendroidserver.test;

import static org.easymock.classextension.EasyMock.*;

import junit.framework.Assert;
import itu.malta.drunkendroidserver.GridCell;
import itu.malta.drunkendroidserver.util.xstreem.converters.MoodMapConverter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

/**
 * @author tobiasmj
 *
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
	 * Test method for {@link itu.malta.drunkendroidserver.util.xstreem.converters.MoodMapConverter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)}.
	 */
	@Test
	public void testMarshal() {
	XStream xStream = new XStream();
	xStream.registerConverter(new MoodMapConverter());
	xStream.alias("MoodMapReading",GridCell.class);
	
	GridCell gc = (GridCell)xStream.fromXML("<MoodMapReading><MoodMapValue>120</MoodMapValue><MoodMapLongitude>14.4889622588559</MoodMapLongitude><MoodMapLatitude>35.9237275622272</MoodMapLatitude></MoodMapReading>");
	GridCell tgc = new GridCell(14.4889622588559, 35.9237275622272);
	tgc.addValue(120);
	
	Assert.assertEquals(gc.getAverage(), tgc.getAverage());
	Assert.assertEquals(gc.getLatitude(), tgc.getLatitude(), 0.0);
	Assert.assertEquals(gc.getLongitude(), tgc.getLongitude(), 0.0);

	}

	/**
	 * Test method for {@link itu.malta.drunkendroidserver.util.xstreem.converters.MoodMapConverter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)}.
	 */
	@Test
	public void testUnmarshal() {
		GridCell cc = createMock(GridCell.class);
		expect(cc.getLatitude()).andStubReturn(35.9237275622272);
		expect(cc.getLongitude()).andStubReturn(14.4889622588559);
		expect(cc.getAverage()).andStubReturn(120);
		replay(cc);

		XStream xStream = new XStream();
		xStream.registerConverter(new MoodMapConverter());
		xStream.alias("MoodMapReading", cc.getClass());

		String xmlOutput = xStream.toXML(cc);
		xmlOutput = xmlOutput.replaceAll("\n", "").replaceAll(" ", "");
		Assert.assertEquals("<MoodMapReading><MoodMapValue>120</MoodMapValue><MoodMapLongitude>14.4889622588559</MoodMapLongitude><MoodMapLatitude>35.9237275622272</MoodMapLatitude></MoodMapReading>",xmlOutput);
		
	}

	/**
	 * Test method for {@link itu.malta.drunkendroidserver.util.xstreem.converters.MoodMapConverter#canConvert(java.lang.Class)}.
	 */
	@Test
	public void testCanConvert() {
		MoodMapConverter mmc = new MoodMapConverter();
		
		GridCell cc = createMock(GridCell.class);
		expect(cc.getLatitude()).andStubReturn(35.9237275622272);
		expect(cc.getLongitude()).andStubReturn(14.4889622588559);
		expect(cc.getAverage()).andStubReturn(120);
		replay(cc);

		Assert.assertEquals(true,mmc.canConvert(GridCell.class));
	}

}
