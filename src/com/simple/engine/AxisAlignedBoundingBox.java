package com.simple.engine;

public class AxisAlignedBoundingBox extends Component {
	
	public AxisAlignedBoundingBox(GameObject gameObject) {
		super("aabb", gameObject);
	}
	
	@Override
	public void update() {
		GamePhysic.addAxisAlignedBoundingBox(this);
	}

}
