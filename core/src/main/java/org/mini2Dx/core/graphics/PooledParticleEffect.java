/**
 * Copyright (c) 2015 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.core.graphics;

import org.mini2Dx.core.game.GameContainer;

/**
 * An instance of {@link ParticleEffect} that can be returned to a
 * {@link ParticleEffectPool} for re-use.
 */
public class PooledParticleEffect implements ParticleEffect {
	private final ParticleEffectPool pool;
	private final ParticleEffect effect;

	public PooledParticleEffect(ParticleEffectPool pool, ParticleEffect effect) {
		super();
		this.effect = effect;
		this.pool = pool;
	}

	public void dispose() {
		pool.free(this);
	}

	@Override
	public void update(GameContainer gc, float delta) {
		effect.update(gc, delta);
	}

	@Override
	public void interpolate(GameContainer gc, float alpha) {
		effect.interpolate(gc, alpha);
	}

	@Override
	public void render(Graphics g) {
		effect.render(g);
	}

	@Override
	public void scaleEffect(float scaleFactor) {
		effect.scaleEffect(scaleFactor);
	}

	@Override
	public void start() {
		effect.start();
	}

	@Override
	public void reset() {
		effect.reset();
	}

	@Override
	public boolean isComplete() {
		return effect.isComplete();
	}

	@Override
	public void setDuration(int duration) {
		effect.setDuration(duration);
	}

	@Override
	public void setPosition(float x, float y) {
		effect.setPosition(x, y);
	}

	@Override
	public void setFlip(boolean flipX, boolean flipY) {
		effect.setFlip(flipX, flipY);
	}

	@Override
	public void flipX() {
		effect.flipX();
	}

	@Override
	public void flipY() {
		effect.flipY();
	}

	@Override
	public boolean isFlipX() {
		return effect.isFlipX();
	}

	@Override
	public boolean isFlipY() {
		return effect.isFlipY();
	}
}
