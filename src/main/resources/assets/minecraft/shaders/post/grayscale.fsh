#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform GrayscaleConfig {
    float GrayscaleStrength;
    float LightCancel;
    float Gamma;
};

out vec4 fragColor;

// Standard luminance coefficients for grayscale
const vec3 Gray = vec3(0.299, 0.587, 0.114);

void main() {
    vec4 color = texture(InSampler, texCoord);

    // --- 1. Расширенная маска оранжево-жёлтых оттенков ---
    bool warmColor =
    (color.r > 0.5) &&          // красный высокий
    (color.g > 0.25) &&         // зелёный средний/высокий
    (color.b < 0.35) &&         // синий низкий
    (abs(color.r - color.g) > 0.1 || abs(color.g - color.b) > 0.1); // не серый

    if (warmColor) {
        fragColor = color;
        return;
    }

    // --- 2. Гамма-коррекция (делает картинку светлее) ---
    vec3 brighter = pow(color.rgb, vec3(1.0 / Gamma));

    // --- 3. Яркость пикселя ---
    float brightness = max(max(brighter.r, brighter.g), brighter.b);

    // --- 4. Уменьшение грейскейла от яркости ---
    float grayFactor = clamp(GrayscaleStrength - brightness * LightCancel, 0.0, 1.0);

    // --- 5. Чистый grayscale ---
    float luma = dot(brighter, Gray);
    vec3 grayColor = vec3(luma);

    // --- 6. Смешиваем цвет и grayscale ---
    vec3 mixed = mix(brighter, grayColor, grayFactor);

    // --- 7. Финально — чёрно-белое, но с полутоном ---
    float finalLuma = dot(mixed, Gray);
    fragColor = vec4(vec3(finalLuma), color.a);
}
