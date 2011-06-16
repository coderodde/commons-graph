package org.apache.commons.graph.domain.basic;

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

import static java.util.Collections.unmodifiableSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.graph.DirectedGraph;
import org.apache.commons.graph.GraphException;
import org.apache.commons.graph.MutableDirectedGraph;
import org.apache.commons.graph.Vertex;
import org.apache.commons.graph.WeightedEdge;
import org.apache.commons.graph.WeightedGraph;
import org.apache.commons.graph.contract.Contract;

/**
 * Description of the Class
 */
public class DirectedGraphImpl<V extends Vertex, WE extends WeightedEdge<V>>
    implements DirectedGraph<V, WE>, WeightedGraph<V, WE>, MutableDirectedGraph<V, WE>
{

    private V root = null;

    private final Set<V> vertices = new HashSet<V>();

    private final Set<WE> edges = new HashSet<WE>();

    private final List<Contract<V, WE>> contracts = new ArrayList<Contract<V, WE>>();

    private final Map<V, Set<WE>> inbound = new HashMap<V, Set<WE>>(); // VERTEX X SET( EDGE )

    private final Map<V, Set<WE>> outbound = new HashMap<V, Set<WE>>(); // VERTEX X SET( EDGE )

    private final Map<WE, V> edgeSource = new HashMap<WE, V>(); // EDGE X VERTEX

    private final Map<WE, V> edgeTarget = new HashMap<WE, V>(); // EDGE X TARGET

    private final Map<WE, Number> edgeWeights = new HashMap<WE, Number>();// EDGE X WEIGHT

    /**
     * Constructor for the DirectedGraphImpl object
     */
    public DirectedGraphImpl()
    {
    }

    /**
     * Constructor for the DirectedGraphImpl object
     *
     * @param dg
     */
    public DirectedGraphImpl( DirectedGraph<V, WE> dg )
    {

        Iterator<V> v = dg.getVertices().iterator();
        while ( v.hasNext() )
        {
            addVertexI( v.next() );
        }

        Iterator<WE> e = dg.getEdges().iterator();
        while ( e.hasNext() )
        {
            WE edge = e.next();
            addEdgeI( edge, edge.getHead(), edge.getTail() );

            if ( dg instanceof WeightedGraph )
            {
                setWeight( edge, edge.getWeight() );
            }
        }
    }

    /**
     * TODO fill me
     *
     * @param c
     * @throws GraphException
     */
    public void addContract( Contract<V, WE> c )
        throws GraphException
    {
        c.setImpl( this );
        c.verify();
        contracts.add( c );
    }

    /**
     * TODO fill me
     *
     * @param c
     */
    public void removeContract( Contract<V, WE> c )
    {
        contracts.remove( c );
    }

    /**
     * TODO fill me
     *
     * @param e
     * @param value
     */
    public void setWeight( WE e, Number value )
    {
        if ( edgeWeights.containsKey( e ) )
        {
            edgeWeights.remove( e );
        }
        edgeWeights.put( e, value );
    }

    // Interface Methods
    // Graph
    /**
     * {@inheritDoc}
     */
    public Set<V> getVertices()
    {
        return unmodifiableSet( vertices );
    }

    /**
     * {@inheritDoc}
     */
    public Set<V> getVertices( WE e )
    {
        Set<V> RC = new HashSet<V>();
        if ( edgeSource.containsKey( e ) )
        {
            RC.add( edgeSource.get( e ) );
        }

        if ( edgeTarget.containsKey( e ) )
        {
            RC.add( edgeTarget.get( e ) );
        }

        return RC;
    }

    /**
     * {@inheritDoc}
     */
    public Set<WE> getEdges()
    {
        return unmodifiableSet( edges );
    }

    /**
     * {@inheritDoc}
     */
    public Set<WE> getEdges( V v )
    {
        Set<WE> RC = new HashSet<WE>();
        if ( inbound.containsKey( v ) )
        {
            RC.addAll( inbound.get( v ) );
        }

        if ( outbound.containsKey( v ) )
        {
            RC.addAll( outbound.get( v ) );
        }

        return RC;
    }

    // Directed Graph
    /**
     * {@inheritDoc}
     */
    public V getSource( WE e )
    {
        return edgeSource.get( e );
    }

    /**
     * {@inheritDoc}
     */
    public V getTarget( WE e )
    {
        return edgeTarget.get( e );
    }

    /**
     * {@inheritDoc}
     */
    public Set<WE> getInbound( Vertex v )
    {
        if ( inbound.containsKey( v ) )
        {
            return unmodifiableSet( inbound.get( v ) );
        }
        return new HashSet<WE>();
    }

    /**
     * {@inheritDoc}
     */
    public Set<WE> getOutbound( Vertex v )
    {
        if ( outbound.containsKey( v ) )
        {
            return unmodifiableSet( outbound.get( v ) );
        }
        return new HashSet<WE>();
    }

    // MutableDirectedGraph
    /**
     * TODO fill me
     */
    private void addVertexI( V v )
        throws GraphException
    {
        if ( root == null )
            root = v;

        vertices.add( v );
    }

    /**
     * {@inheritDoc}
     */
    public void addVertex( V v )
        throws GraphException
    {
        Iterator<Contract<V, WE>> conts = contracts.iterator();
        while ( conts.hasNext() )
        {
            conts.next().addVertex( v );
        }
        addVertexI( v );
    }

    /**
     * TODO fill me
     *
     * @param v
     * @throws GraphException
     */
    private void removeVertexI( Vertex v )
        throws GraphException
    {
        try
        {
            vertices.remove( v );
        }
        catch ( Exception ex )
        {
            throw new GraphException( ex );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeVertex( Vertex v )
        throws GraphException
    {
        Iterator<Contract<V, WE>> conts = contracts.iterator();
        while ( conts.hasNext() )
        {
            conts.next().removeVertex( v );
        }

        removeVertexI( v );
    }

    /**
     * TODO fill me
     *
     * @param e
     * @param start
     * @param end
     * @throws GraphException
     */
    private void addEdgeI( WE e, V start, V end )
        throws GraphException
    {
        edges.add( e );

        edgeWeights.put( e, e.getWeight() );

        edgeSource.put( e, start );
        edgeTarget.put( e, end );

        if ( !outbound.containsKey( start ) )
        {
            Set<WE> edgeSet = new HashSet<WE>();
            edgeSet.add( e );

            outbound.put( start, edgeSet );
        }
        else
        {
            outbound.get( start ).add( e );
        }

        if ( !inbound.containsKey( end ) )
        {
            Set<WE> edgeSet = new HashSet<WE>();
            edgeSet.add( e );

            inbound.put( end, edgeSet );
        }
        else
        {
            inbound.get( end ).add( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addEdge( WE e, V start, V end )
        throws GraphException
    {
        Iterator<Contract<V, WE>> conts = contracts.iterator();
        while ( conts.hasNext() )
        {
            Contract<V, WE> cont = conts.next();

            cont.addEdge( e, start, end );
        }

        addEdgeI( e, start, end );
    }

    /**
     * TODO fill me
     *
     * @param e
     * @throws GraphException
     */
    private void removeEdgeI( WE e )
        throws GraphException
    {
        try
        {
            Set<WE> edgeSet = null;

            V source = edgeSource.get( e );
            edgeSource.remove( e );
            edgeSet = outbound.get( source );
            edgeSet.remove( e );

            V target = edgeTarget.get( e );
            edgeTarget.remove( e );
            edgeSet = inbound.get( target );
            edgeSet.remove( e );

            if ( edgeWeights.containsKey( e ) )
            {
                edgeWeights.remove( e );
            }
        }
        catch ( Exception ex )
        {
            throw new GraphException( ex );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeEdge( WE e )
        throws GraphException
    {
        Iterator<Contract<V, WE>> conts = contracts.iterator();
        while ( conts.hasNext() )
        {
            conts.next().removeEdge( e );
        }
        removeEdgeI( e );
    }

    // WeightedGraph
    /**
     * {@inheritDoc}
     */
    public Number getWeight( WE e )
    {
        if ( edgeWeights.containsKey( e ) )
        {
            return edgeWeights.get( e );
        }
        return 1.0;
    }

}