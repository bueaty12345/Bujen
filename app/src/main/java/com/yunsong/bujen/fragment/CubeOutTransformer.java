package com.yunsong.bujen.fragment;

import android.view.View;

public class CubeOutTransformer extends ABaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		// 设置透视效果的原点
		view.setPivotX(position < 0f ? view.getWidth() : 0f);
		view.setPivotY(view.getHeight() * 0.5f);

		// 设置Y轴旋转角度更小，减少旋转幅度
		float rotationAngle = 60f * position;  // 将原来的90度减少到60度
		view.setRotationY(rotationAngle);

		// 设置透明度：透明度随着页面的位置变化
		float alpha = 1 - Math.abs(position);  // 透明度随着position增大而减小
		view.setAlpha(alpha);  // 设置页面透明度
	}

	@Override
	public boolean isPagingEnabled() {
		return true;
	}
}
