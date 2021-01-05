package com.simple.engine;

public class Font {

	private static final int START_LETTER_COLOR_CODE = 0xff0000ff;

	private static final int END_LETTER_COLOR_CODE = 0xffffff00;

	public static final int RENDER_COLOR_CODE = 0xffffffff;
	
	private Image image;
	
	private int[] offsets;
	
	private int[] widths;
	
	public Font(String path) {
		 this.image = new Image(path);
		 this.offsets = new int[59];
		 this.widths = new int[59];
		 
		 int letterIndex = 0;
		 for (int i = 0; i < this.image.getWidth(); i++) {
			if (this.image.getPixels()[i] == START_LETTER_COLOR_CODE) {
				this.offsets[letterIndex] = i;
			}
			if (this.image.getPixels()[i] == END_LETTER_COLOR_CODE) {
				this.widths[letterIndex] = i - this.offsets[letterIndex];
				letterIndex++;
			}
		}
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image fontImg) {
		this.image = fontImg;
	}

	public int[] getOffsets() {
		return offsets;
	}

	public void setOffsets(int[] offsets) {
		this.offsets = offsets;
	}

	public int[] getWidths() {
		return widths;
	}

	public void setWidths(int[] widths) {
		this.widths = widths;
	}
	
}
