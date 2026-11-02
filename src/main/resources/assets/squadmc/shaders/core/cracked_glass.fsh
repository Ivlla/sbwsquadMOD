#version 150

uniform float Seed;
uniform float Amount;
uniform float Aspect;

in vec2 uv;
out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7)) + Seed) * 43758.5453);
}

vec2 hash2(vec2 p) {
    return vec2(hash(p), hash(p + 19.19));
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);
}

float fbm(vec2 p) {
    return 0.55 * noise(p) + 0.3 * noise(p * 2.1 + 5.2) + 0.15 * noise(p * 4.3 + 9.1);
}

float segment(vec2 p, vec2 a, vec2 b, out float along) {
    vec2 ab = b - a;
    along = clamp(dot(p - a, ab) / dot(ab, ab), 0.0, 1.0);
    return length(p - a - ab * along);
}

// Трещина: ломаная из двух отрезков, толщина рваная и сходит на нет к концу.
float crack(vec2 p, vec2 a, vec2 mid, vec2 b, float width) {
    float t1, t2;
    float d1 = segment(p, a, mid, t1);
    float d2 = segment(p, mid, b, t2);
    float t = d1 < d2 ? t1 * 0.5 : 0.5 + t2 * 0.5;
    float d = min(d1, d2);
    float rough = 0.45 + 1.1 * fbm(p * 26.0);
    float w = width * rough * (1.0 - t * 0.75);
    return 1.0 - smoothstep(w * 0.45, w * 1.5, d);
}

float cellEdge(vec2 p) {
    vec2 cell = floor(p);
    vec2 local = fract(p);
    float first = 8.0;
    float second = 8.0;
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 offset = vec2(float(x), float(y));
            vec2 r = offset + hash2(cell + offset) - local;
            float d = dot(r, r);
            if (d < first) {
                second = first;
                first = d;
            } else if (d < second) {
                second = d;
            }
        }
    }
    return sqrt(second) - sqrt(first);
}

void main() {
    vec2 p = vec2((uv.x - 0.5) * Aspect, uv.y - 0.5);
    vec2 impact = (hash2(vec2(3.0, 7.0)) - 0.5) * vec2(0.22 * Aspect, 0.22);
    vec2 d = p - impact;
    float radius = length(d);
    float cracks = 0.0;

    // Длинные лучи от точки удара.
    for (int i = 0; i < 9; i++) {
        float fi = float(i);
        float angle = (fi + 1.1 * hash(vec2(fi, 1.0))) * 6.2832 / 9.0;
        float reach = 0.35 + 0.85 * hash(vec2(fi, 2.0));
        float bend = (hash(vec2(fi, 3.0)) - 0.5) * 0.45;
        vec2 dir = vec2(cos(angle), sin(angle));
        vec2 dir2 = vec2(cos(angle + bend), sin(angle + bend));
        vec2 mid = impact + dir * reach * 0.5;
        cracks = max(cracks, crack(p, impact, mid, mid + dir2 * reach * 0.5, 0.0025 + 0.0035 * hash(vec2(fi, 4.0))));
    }

    // Отдельные трещины поперёк стекла.
    for (int i = 0; i < 5; i++) {
        float fi = float(i);
        vec2 a = (hash2(vec2(fi, 11.0)) - 0.5) * vec2(Aspect, 1.0) * 1.3;
        vec2 b = (hash2(vec2(fi, 13.0)) - 0.5) * vec2(Aspect, 1.0) * 1.3;
        vec2 mid = mix(a, b, 0.5) + (hash2(vec2(fi, 17.0)) - 0.5) * 0.12;
        cracks = max(cracks, crack(p, a, mid, b, 0.0036) * 0.8);
    }

    // Рваная сетка мелких сколов вокруг удара: местами линия пропадает, местами расплывается.
    vec2 warp = p + (vec2(fbm(p * 7.0), fbm(p * 7.0 + 31.0)) - 0.5) * 0.09;
    float nearImpact = 1.0 - smoothstep(0.1, 0.42, radius);
    float gaps = smoothstep(0.42, 0.62, fbm(p * 9.0 + 3.0));
    float chips = (1.0 - smoothstep(0.0, 0.05 + 0.1 * fbm(p * 24.0), cellEdge(warp * 11.0))) * gaps * nearImpact;
    cracks = max(cracks, chips);

    // Тёмное пятно размытия вдоль сетки у самого центра.
    float smudge = (1.0 - smoothstep(0.0, 0.2, radius)) * gaps * 0.3;

    float alpha = max(cracks * 0.9, smudge) * Amount;
    fragColor = vec4(vec3(0.015, 0.018, 0.025), alpha);
}
