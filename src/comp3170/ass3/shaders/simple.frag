#version 410

uniform sampler2D u_texture;
uniform float u_alpha;
uniform bool u_debugNormals;

uniform vec3 u_lightDirection;   // direction TO the sun (world space)
uniform vec3 u_lightColour;      // sun colour 
uniform vec3 u_ambientColour;    // ambience 

in vec3 v_normal;
in vec2 v_uv;

out vec4 o_colour;

void main() {
    if (u_debugNormals) {
        o_colour = vec4(v_normal, 1.0);
        return;
    }    
    vec4 texColour = texture(u_texture, v_uv);

//difuse
	vec3 normal = normalize(v_normal);
	float diffuse = max(dot(normal, u_lightDirection), 0.0);
	
	vec3 lighting = u_ambientColour + u_lightColour * diffuse; //ambient
	
	vec3 texturecolour = texColour.rgb * lighting;  //colour or the texture
	
	o_colour = vec4(texturecolour, texColour.a * u_alpha);
}