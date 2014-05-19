package com.tf.model;

public enum ElementType {
	/**
	 * 素材类型枚举类:文本
	 */
	text(1),
	/**
	 * 素材类型枚举类 ：音频
	 */

	audio(2),
	/**
	 * 素材类型枚举类 ：图片
	 */

	image(3),
	/**
	 * 素材类型枚举类 ：视频
	 */
	

	video(4),

	
	/**
	 * 素材类型枚举类 ：网页
	 */

	html(5),
	/**
	 * 素材类型枚举类 ：flsh
	 */

	flash(6),
	/**
	 * 素材类型枚举类 ：office文件、txt、pdf
	 */

	document(7),
	/**
	 * 素材类型枚举类 ：流媒体
	 */

	stream(8),
	/**
	 * 素材类型枚举类 ：其他
	 */

	other(9);
	
	private int value;

	ElementType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public String getType() {
		return this.name();
	}

	public ElementType setValue(int value) {
		this.value = value;
		return this;
	}

}
