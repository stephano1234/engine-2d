package com.simple.engine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundClip {

	private Clip clip;
	
	private FloatControl audioControl;
	
	public SoundClip(String path) {
		try {
			InputStream audioSrc = SoundClip.class.getResourceAsStream(path);
			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			AudioInputStream audio = AudioSystem.getAudioInputStream(bufferedIn);
			AudioFormat baseFormat = audio.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
					baseFormat.getSampleRate(), 
					16, 
					baseFormat.getChannels(), 
					baseFormat.getChannels() * 2, 
					baseFormat.getSampleRate(), 
					false);
			AudioInputStream decodedAudio = AudioSystem.getAudioInputStream(decodeFormat, audio);
			this.clip = AudioSystem.getClip();
			this.clip.open(decodedAudio);
			// the master gain controls the volume by adding or subtracting the audio decibels (dB)
			this.audioControl = (FloatControl) this.clip.getControl(FloatControl.Type.MASTER_GAIN);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void play() {
		if (this.clip == null) {
			return;
		}
		this.stop();
		this.clip.setFramePosition(0);
		// this loop guarantees that the sound will start playing, 
		// even if for some reason it doesn't play at first  
		while (!this.clip.isRunning()) {
			this.clip.start();
		}
	}

	public void stop() {
		if (this.clip.isRunning()) {
			this.clip.stop();
		}
	}
	
	public void close() {
		this.stop();
		this.clip.drain();
		this.clip.close();
	}
	
	public void loop() {
		this.clip.loop(Clip.LOOP_CONTINUOUSLY);
		this.play();
	}
	
	public void setVolume(float value) {
		this.audioControl.setValue(value);
	}
	
	public boolean isRunning() {
		return this.clip.isRunning();
	}
	
}
