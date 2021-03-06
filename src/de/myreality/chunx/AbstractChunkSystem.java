/*
 * chunx is a Java 2D chunk engine to generate "infinite" worlds.
 * Copyright (C) 2013  Miguel Gonzalez
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.myreality.chunx;

import java.util.Collection;

import de.myreality.chunx.concurrent.ConcurrentMatrixList;
import de.myreality.chunx.io.ChunkLoader;
import de.myreality.chunx.io.ChunkSaver;
import de.myreality.chunx.moving.MovementListenerBinder;
import de.myreality.chunx.util.AbstractManageable;
import de.myreality.chunx.util.ChunkTargetBinder;
import de.myreality.chunx.util.MatrixList;
import de.myreality.chunx.util.Observable;
import de.myreality.chunx.util.PositionInterpreter;
import de.myreality.chunx.util.SimpleObservable;
import de.myreality.chunx.util.SimplePositionInterpreter;

/**
 * Abstract implementation of {@link ChunkSystem}
 * 
 * @author Miguel Gonzalez <miguel-gonzalez@gmx.de>
 * @since 1.0
 * @version 1.0
 */
public abstract class AbstractChunkSystem extends AbstractManageable implements
		ChunkSystem {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final long serialVersionUID = 1L;
	
	// ===========================================================
	// Fields
	// ===========================================================

	private ChunkConfiguration configuration;
	
	private ChunkHandler chunkHandler;

	protected MatrixList<Chunk> chunks;
	
	protected PositionInterpreter positionInterpreter;
	
	private ChunkLoader chunkLoader;
	
	private ChunkSaver chunkSaver;
	
	private MovementListenerBinder movementBinder;
	
	private ChunkTargetBinder targetBinder;
	
	private Observable<ChunkSystemListener> observable;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public AbstractChunkSystem(ChunkConfiguration configuration) {
		this.configuration = configuration;
		observable = new SimpleObservable<ChunkSystemListener>();
		chunks = new ConcurrentMatrixList<Chunk>();
		positionInterpreter = new SimplePositionInterpreter(configuration);
		movementBinder = new MovementListenerBinder(this);
		targetBinder = new ChunkTargetBinder();
		addListener(movementBinder);
		addListener(targetBinder);
	}

	// ===========================================================
	// Getters and Setters
	// ===========================================================

	// ===========================================================
	// Methods from Superclass
	// ===========================================================

	@Override
	public void update() {
		update(0.0f);
	}

	@Override
	public ChunkConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(ChunkConfiguration configuration) {
		this.configuration = configuration;
		positionInterpreter = new SimplePositionInterpreter(configuration);
	}

	@Override
	public Chunk getActiveChunk() {
		ChunkTarget focused = configuration.getFocused();
		
		if (focused != null) {
			
			final int INDEX_X = positionInterpreter.translateX(focused.getX());
			final int INDEX_Y = positionInterpreter.translateY(focused.getY());
			
			return chunks.get(INDEX_X, INDEX_Y);
		} else {
			return null;
		}
	}

	@Override
	public Collection<Chunk> getChunks() {		
		return chunks;
	}

	@Override
	public Chunk getChunk(int indexX, int indexY) {
		return chunks.get(indexX, indexY);
	}

	@Override
	public int getCurrentChunkCount() {
		return chunks.size();
	}

	@Override
	public void addListener(ChunkSystemListener listener) {
		observable.addListener(listener);
	}

	@Override
	public void removeListener(ChunkSystemListener listener) {
		observable.removeListener(listener);
	}

	@Override
	public boolean hasListener(ChunkSystemListener listener) {
		return observable.hasListener(listener);
	}

	@Override
	public void setHandler(ChunkHandler handler) {
		
		if (handler != null) {
			
			if (this.chunkHandler != null) {
				movementBinder.removeListener(chunkHandler);
			}
			
			this.chunkHandler = handler;
			
			movementBinder.addListener(chunkHandler);			
		}
	}

	@Override
	public ChunkHandler getHandler() {
		return chunkHandler;
	}

	@Override
	public Collection<ChunkSystemListener> getListeners() {
		return observable.getListeners();
	}
	
	@Override
	public void setLoader(ChunkLoader chunkLoader) {
		if (chunkLoader != null) {
			this.chunkLoader = chunkLoader;
		}
	}

	@Override
	public ChunkLoader getLoader() {
		return chunkLoader;
	}

	@Override
	public void setSaver(ChunkSaver chunkSaver) {
		if (chunkSaver != null) {
			this.chunkSaver = chunkSaver;
		}
	}

	@Override
	public ChunkSaver getSaver() {
		return chunkSaver;
	}
	
	
	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner classes
	// ===========================================================
}
