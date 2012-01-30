package org.apache.commons.graph.export;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.graph.Edge;
import org.apache.commons.graph.Graph;
import org.apache.commons.graph.Labeled;
import org.apache.commons.graph.Vertex;
import org.apache.commons.graph.Weighted;

final class GraphMLExporter<V extends Vertex, E extends Edge, G extends Graph<V, E>>
    extends AbstractExporter<V, E, G>
{

    private static final String GRAPHML = "graphml";

    private static final String XMLNS = "xmlns";

    private static final String GRAPHML_XMLNS = "http://graphml.graphdrawing.org/xmlns";

    private static final String EDGEDEFAULT = "edgedefault";

    private static final String DIRECTED = "directed";

    private static final String KEY = "key";

    private static final String FOR = "for";

    private static final String ID = "id";

    private static final String ATTR_NAME = "attr.name";

    private static final String ATTR_TYPE = "attr.type";

    private static final String GRAPH = "graph";

    private static final String NODE = "node";

    private static final String EDGE = "edge";

    private static final String SOURCE = "source";

    private static final String TARGET = "target";

    private static final String DATA = "data";

    private static final String LABEL = "label";

    private static final String STRING = "string";

    private static final String FLOAT = "float";

    private static final String DOUBLE = "double";

    private static final String LONG = "long";

    private static final String BOOLEAN = "boolean";

    private static final String INT = "int";

    private static final String WEIGHT = "weight";

    public GraphMLExporter( G graph, Writer writer, String name )
    {
        super( graph, writer, name );
    }

    @Override
    protected void internalExport()
        throws Exception
    {
        // scan graph and print TYPES description for vertex AND edges' properties
        Map<String, String> edgeKeyTypes = new HashMap<String, String>();

        for ( E edge : getGraph().getEdges() )
        {
            if ( edge instanceof Labeled )
            {
                if ( !edgeKeyTypes.containsKey( LABEL ) )
                {
                    edgeKeyTypes.put( LABEL, getStringType( ( (Labeled) edge ).getLabel() ) );
                }
            }

            if ( edge instanceof Weighted )
            {
                if ( !edgeKeyTypes.containsKey( WEIGHT ) )
                {
                    edgeKeyTypes.put( WEIGHT, getStringType( ( (Weighted<?>) edge ).getWeight() ) );
                }
            }
        }

        // scan vertices
        Map<String, String> vertexKeyTypes = new HashMap<String, String>();

        for ( V vertex : getGraph().getVertices() )
        {
            if ( vertex instanceof Labeled )
            {
                if ( !vertexKeyTypes.containsKey( LABEL ) )
                {
                    vertexKeyTypes.put( LABEL, getStringType( ( (Labeled) vertex ).getLabel() ) );
                }
            }
        }

        XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter( getWriter() );

        // start document tokens
        xmlWriter.writeStartDocument();
        xmlWriter.writeStartElement( GRAPHML );
        xmlWriter.writeAttribute( XMLNS, GRAPHML_XMLNS );

        xmlWriter.writeStartElement( GRAPH );
        xmlWriter.writeAttribute( ID, getName() );
        xmlWriter.writeAttribute( EDGEDEFAULT, DIRECTED );

        // write EDGE key types
        for ( Map.Entry<String, String> entry : edgeKeyTypes.entrySet() )
        {
            xmlWriter.writeStartElement( KEY );
            xmlWriter.writeAttribute( ID, entry.getKey() );
            xmlWriter.writeAttribute( FOR, EDGE );
            xmlWriter.writeAttribute( ATTR_NAME, entry.getKey() );
            xmlWriter.writeAttribute( ATTR_TYPE, entry.getValue() );
            xmlWriter.writeEndElement();
        }

        // write VERTICES' key types
        for ( Map.Entry<String, String> entry : vertexKeyTypes.entrySet() )
        {
            xmlWriter.writeStartElement( KEY );
            xmlWriter.writeAttribute( ID, entry.getKey() );
            xmlWriter.writeAttribute( FOR, NODE );
            xmlWriter.writeAttribute( ATTR_NAME, entry.getKey() );
            xmlWriter.writeAttribute( ATTR_TYPE, entry.getValue() );
            xmlWriter.writeEndElement();
        }

        for ( V vertex : getGraph().getVertices() )
        {
            xmlWriter.writeStartElement( NODE );
            xmlWriter.writeAttribute( ID, String.valueOf( vertex.hashCode() ) );

            if ( vertex instanceof Labeled )
            {
                String label = ( (Labeled) vertex ).getLabel();
                xmlWriter.writeStartElement( DATA );
                xmlWriter.writeAttribute( KEY, LABEL );
                xmlWriter.writeCharacters( label );
                xmlWriter.writeEndElement();
            }

            xmlWriter.writeEndElement();
        }

        for ( E edge : getGraph().getEdges() )
        {
            xmlWriter.writeStartElement( EDGE );
            xmlWriter.writeAttribute( ID, String.valueOf( edge.hashCode() ) );
            xmlWriter.writeAttribute( SOURCE, String.valueOf( getGraph().getVertices( edge ).getHead().hashCode() ) );
            xmlWriter.writeAttribute( TARGET, String.valueOf( getGraph().getVertices( edge ).getTail().hashCode() ) );

            if ( edge instanceof Labeled )
            {
                xmlWriter.writeAttribute( LABEL, ( (Labeled) edge ).getLabel() );
            }
            if ( edge instanceof Weighted )
            {
                xmlWriter.writeAttribute( WEIGHT, ( (Weighted<?>) edge ).getWeight().toString() );
            }
            xmlWriter.writeEndElement();
        }

        xmlWriter.writeEndElement(); // graph
        xmlWriter.writeEndElement(); // graphml
        xmlWriter.writeEndDocument();

        xmlWriter.flush();
        xmlWriter.close();
    }

    private static <O> String getStringType( O object )
    {
        if ( object instanceof Integer )
        {
            return INT;
        }
        else if ( object instanceof Long )
        {
            return LONG;
        }
        else if ( object instanceof Float )
        {
            return FLOAT;
        }
        else if ( object instanceof Double )
        {
            return DOUBLE;
        }
        else if ( object instanceof Boolean )
        {
            return BOOLEAN;
        }
        return STRING;
    }

}
