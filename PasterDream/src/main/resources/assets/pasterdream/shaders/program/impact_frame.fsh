#version 150

uniform sampler2D DiffuseSampler;
uniform float threshold;
uniform float thresholdLerp;
uniform float invert;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // 计算像素亮度灰度（取 RGB 最大值作为近似灰度）
    float gray = max(color.r, max(color.g, color.b));

    // 阈值判定：超过阈值视为"冲击高亮"（纯白），否则按 lerp 衰减
    float g;
    if (gray > threshold) {
        g = 1.0;
    } else {
        if (thresholdLerp > 0.0) {
            float v = threshold - gray;
            g = smoothstep(0.0, 1.0, 1.0 - clamp(v / thresholdLerp, 0.0, 1.0));
        } else {
            g = 0.0;
        }
    }

    // 反相模式（invert > 0 时灰闪反转为"黑场冲击"）
    if (invert > 0.0) {
        g = 1.0 - g;
    }

    fragColor = vec4(g, g, g, color.a);
}
