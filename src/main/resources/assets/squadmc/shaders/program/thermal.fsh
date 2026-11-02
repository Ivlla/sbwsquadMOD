#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float Time;
uniform float Palette;
uniform float PixelSize;
uniform float Noise;
uniform float Scanlines;
uniform float Softness;
uniform float Contrast;

in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

// Тёплые объекты отрисованы лайтмапой без синего канала, а нагрев лежит в отношении G к R.
float temperature(vec2 uv) {
    vec3 c = texture(DiffuseSampler, uv).rgb;
    float luma = dot(c, vec3(0.299, 0.587, 0.114));
    float cold = 0.05 + 0.32 * luma;
    if (c.b < 0.006 && c.r > 0.01) {
        float heat = clamp(c.g / c.r, 0.0, 1.0);
        return mix(cold, 0.55 + 0.45 * clamp(c.r * 1.6, 0.0, 1.0), heat);
    }
    return cold;
}

vec3 palette(float t) {
    if (Palette < 0.5) return vec3(t);
    if (Palette < 1.5) return vec3(1.0 - t);
    return vec3(t * t * 0.55, t, t * t * 0.45) * vec3(0.9, 1.0, 0.85);
}

void main() {
    vec2 grid = max(PixelSize, 1.0) / OutSize;
    vec2 uv = (floor(texCoord / grid) + 0.5) * grid;

    float t = temperature(uv);
    if (Softness > 0.0) {
        float around = temperature(uv + vec2(grid.x, 0.0)) + temperature(uv - vec2(grid.x, 0.0))
                     + temperature(uv + vec2(0.0, grid.y)) + temperature(uv - vec2(0.0, grid.y));
        t = mix(t, around * 0.25, Softness * 0.5);
    }

    t = clamp((t - 0.35) * Contrast + 0.35, 0.0, 1.0);
    t += (hash(uv + fract(Time)) - 0.5) * Noise;
    t -= Scanlines * 0.5 * (0.5 + 0.5 * sin(texCoord.y * OutSize.y * 1.57));

    vec2 edge = texCoord * (1.0 - texCoord);
    float vignette = pow(clamp(edge.x * edge.y * 18.0, 0.0, 1.0), 0.2);
    fragColor = vec4(palette(clamp(t * vignette, 0.0, 1.0)), 1.0);
}
