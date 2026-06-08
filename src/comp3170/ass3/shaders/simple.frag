#version 410

uniform sampler2D u_texture;
uniform float u_alpha;
uniform bool u_debugNormals;

uniform vec3 u_lightDirection;   // direction TO the sun (world space)
uniform vec3 u_lightColour;      // sun colour 
uniform vec3 u_ambientColour;    // ambience 

uniform vec3 u_cameraposition;
uniform bool u_shiny;

in vec3 v_normal;
in vec2 v_uv;
in vec3 v_worldpos;

out vec4 o_colour;

void main() {
    if (u_debugNormals) {
        o_colour = vec4(v_normal, 1.0);
        return;
    }    
    vec4 texColour = texture(u_texture, v_uv);
    
	vec3 normal = normalize(v_normal);
	
	//difuse
	
	float diffuse = max(dot(normal, u_lightDirection), 0.0);
	
	//specular:
	vec3 specular = vec3(0.0);
	if(u_shiny) {
		vec3 view = normalize(u_cameraposition - v_worldpos);
        vec3 reflectDir = reflect(-u_lightDirection, normal);
        float spec = pow(max(dot(reflectDir, view), 0.0), 32.0);
        specular = u_lightColour * spec;
    }
	
	vec3 lighting = u_ambientColour + u_lightColour * diffuse; //ambient
	
	vec3 texturecolour = texColour.rgb * lighting + specular;  //colour or the texture
	
	o_colour = vec4(texturecolour, texColour.a * u_alpha);
}