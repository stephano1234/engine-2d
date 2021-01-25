package com.simple.engine;

public class BoundingBox extends Component {
	
	public BoundingBox(GameObject gameObject) {
		super("bb", gameObject);
	}
	
	@Override
	public void update() {
//		GamePhysic.addBoundingBox(this);
	}

}
