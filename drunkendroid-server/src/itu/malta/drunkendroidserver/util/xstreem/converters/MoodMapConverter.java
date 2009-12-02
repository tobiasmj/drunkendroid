package itu.malta.drunkendroidserver.util.xstreem.converters;

import java.lang.reflect.Proxy;
import java.util.LinkedList;

import itu.malta.drunkendroidserver.domain.GridCell;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;


public class MoodMapConverter implements Converter{
	/**
	 * method for marshaling a MoodMap object into XML
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		  GridCell MMReading = (GridCell) value;
		  writer.addAttribute("value", Integer.toString(MMReading.getAverage()));
		  writer.addAttribute("long", Double.toString(MMReading.getLongitude()));
		  writer.addAttribute("lat", Double.toString(MMReading.getLatitude()));
	}
	
	/**
	 * Method for un-marshaling MoodMap points from XML into an objects
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		
		LinkedList<GridCell> points = new LinkedList<GridCell>();
		String longitude = "";
		String latitude = "";
		String value = "";
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("p".equals(reader.getNodeName())) {
                longitude = reader.getAttribute("long");
                latitude = reader.getAttribute("lat");
                value = reader.getAttribute("value");
                    
            }
            reader.moveUp();
            GridCell gc = new GridCell(Double.valueOf(longitude),Double.valueOf(latitude));
    		gc.setValue(Integer.valueOf(value));
    		points.add(gc);
        }
		return points;
	}

	/**
	 * Method to determine what class that this converter can convert.
	 */
	@Override
	public boolean canConvert(Class clazz) {;
		
		//Evil Hack, implemented in order to make the unitTests work with EasyMock-extension.
		// When using the extension for EasyMock, that can Mock directly from classes instead
		// of only from interfaces, CGLIB is used and creates a new class type, which we need 
		// to support as a convertible class.
		String className = clazz.getName();
		String[] classNameSplit = className.split("\\$\\$");
		className = classNameSplit[0];
		// End Evil Hack..
		if (clazz.equals(DynamicProxyMapper.DynamicProxy.class)) {
			return true;
		}
		else if (Proxy.isProxyClass(clazz)) {
			return true;
		}
		else if (clazz.equals(GridCell.class)) {
			return true;
		}
		// Test if the class is enhanced by CGLIB, due to EasyMock-extension.
		else if (className.equals(GridCell.class.getName())) {
			return true;
		}
		else  {
			return false;
		}
		
		
	}
	

}
