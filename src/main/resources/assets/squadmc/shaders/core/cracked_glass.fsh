#version 150

uniform float Seed;
uniform float Amount;
uniform float Aspect;

in vec2 uv;
out vec4 fragColor;

vec2 hash2(vec2 p) {
    p = vec2(dot(p, vec2(127.1, 311.7)), dot(p, vec2(269.5, 183.3)));
    return fract(sin(p + Seed) * 43758.5453);
}

// Расстояние до ближайшей границы осколка и случайное значение осколка.
vec2 shards(vec2 p) {
    vec2 cell = floor(p);
    vec2 local = fract(p);
    vec2 nearest = vec2(0.0);
    vec2 nearestCell = vec2(0.0);
    float best = 8.0;
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 offset = vec2(float(x), float(y));
            vec2 r = offset + hash2(cell + offset) - local;
            float d = dot(r, r);
            if (d < best) {
                best = d;
                nearest = r;
                nearestCell = offset;
            }
        }
    }
    float edge = 8.0;
    for (int y = -2; y <= 2; y++) {
        for (int x = -2; x <= 2; x++) {
            vec2 offset = nearestCell + vec2(float(x), float(y));
            vec2 r = offset + hash2(cell + offset) - local;
            vec2 delta = r - nearest;
            if (dot(delta, delta) > 0.00001) {
                edge = min(edge, dot(0.5 * (nearest + r), normalize(delta)));
            }
        }
    }
    return vec2(edge, hash2(cell + nearestCell).x);
}

void main() {
    vec2 p = vec2((uv.x - 0.5) * Aspect, uv.y - 0.5);
    vec2 impact = (hash2(vec2(3.0, 7.0)) - 0.5) * vec2(0.5 * Aspect, 0.45);
    vec2 d = p - impact;
    float radius = length(d);

    // Деление на радиус вытягивает осколки вдоль лучей от точки удара и мельчит их в центре.
    vec2 warped = d / (radius + 0.16) * 5.0;
    vec2 result = shards(warped);
    float line = 1.0 - smoothstep(0.0, 0.03, result.x);

    vec2 wide = shards(p * 3.5 + 11.0);
    float wideLine = (1.0 - smoothstep(0.0, 0.012, wide.x)) * 0.35;

    float falloff = 1.0 - smoothstep(0.15, 1.05, radius);
    float cracks = max(line * falloff, wideLine * (0.35 + falloff));

    // Осколки по-разному преломляют свет и немного затемняют обзор.
    float tint = (result.y - 0.5) * 0.10 * falloff;
    float dark = 0.10 + 0.22 * falloff * falloff;
    float centre = (1.0 - smoothstep(0.0, 0.07, radius)) * 0.65;

    vec3 color = mix(vec3(0.03) + tint, vec3(0.86, 0.92, 0.95), cracks);
    color = mix(color, vec3(0.92), centre);
    float alpha = max(max(cracks * 0.85, dark + abs(tint)), centre) * Amount;
    fragColor = vec4(color, alpha);
}
