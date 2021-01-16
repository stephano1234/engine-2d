package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public class GamePhysic {

	private static final List<AxisAlignedBoundingBox> axisAlignedBoundingBoxList = new ArrayList<>();
	
	private GamePhysic() {}
	
	public static void addAxisAlignedBoundingBox(AxisAlignedBoundingBox axisAlignedBoundingBox) {
		axisAlignedBoundingBoxList.add(axisAlignedBoundingBox);
	}

	public static void updateAxisAlignedBoundingBox() {
		for (int i = 0; i < axisAlignedBoundingBoxList.size(); i++) {
			GameObject gb0 = axisAlignedBoundingBoxList.get(i).getGameObject();
			for (int j = i + 1; j < axisAlignedBoundingBoxList.size(); j++) {
				GameObject gb1 = axisAlignedBoundingBoxList.get(j).getGameObject();
				if 
					(
					gb0.getPositionX() + gb0.getOffsetX() + gb0.getPaddingLeft() <= gb1.getPositionX() + gb1.getOffsetX() + gb1.getWidth() - gb1.getPaddingRight()
					&&
					gb1.getPositionX() + gb1.getOffsetX() + gb1.getPaddingLeft() <= gb0.getPositionX() + gb0.getOffsetX() + gb0.getWidth() - gb0.getPaddingRight()
					&&
					gb0.getPositionY()  + gb0.getOffsetY() + gb0.getPaddingTop() <= gb1.getPositionY() + gb1.getOffsetY() + gb1.getHeight() - gb1.getPaddingBottom()
					&&
					gb1.getPositionY() + gb1.getOffsetY() + gb1.getPaddingTop() <= gb0.getPositionY() + gb0.getOffsetY() + gb0.getHeight() - gb0.getPaddingBottom()
					)
				{
					gb0.applyAxisAlignedBoundingBoxEvent(gb1);
					gb1.applyAxisAlignedBoundingBoxEvent(gb0);
				}
			}			
		}
		axisAlignedBoundingBoxList.clear();
	}

}
