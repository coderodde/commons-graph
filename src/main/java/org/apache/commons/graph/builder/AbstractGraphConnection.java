package org.apache.commons.graph.builder;

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

import static org.apache.commons.graph.utils.Assertions.checkState;

import org.apache.commons.graph.Edge;
import org.apache.commons.graph.Vertex;

public abstract class AbstractGraphConnection<V extends Vertex, E extends Edge>
    implements GraphConnection<V, E>
{

    private GraphConnector<V, E> connector;

    public final void connect( GraphConnector<V, E> connector )
    {
        checkState( this.connector == null, "Re-entry not allowed!" );
        this.connector = connector;

        try
        {
            connect();
        }
        finally
        {
            this.connector = null;
        }
    }

    protected final V addVertex( V vertex )
    {
        return connector.addVertex( vertex );
    }

    protected final HeadVertexConnector<V, E> addEdge( E edge )
    {
        return connector.addEdge( edge );
    }

    public abstract void connect();

}