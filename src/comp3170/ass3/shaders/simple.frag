#version 410

uniform sampler2D u_texture;
uniform float u_alpha;
uniform bool u_debugNormals;

uniform vec3 u_lightDirection;   // direction TO the sun (world space)
uniform vec3 u_lightColour;      // sun colour 
uniform vec3 u_ambientColour;    // ambience 

uniform vec3 u_cameraposition;
uniform bool u_shiny;

uniform vec3 u_headlightposition;
uniform bool u_daytime;
uniform vec3 u_spotdirection;

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
	
	vec3 lightdir;
    float intensity = 1.0;  
    
	//light direction according to the daytime
	
	if(u_daytime) {
		lightdir = normalize(u_lightDirection);
	} else {
		vec3 towardlight = u_headlightposition - v_worldpos;
        float dist = length(towardlight);
		lightdir = normalize(towardlight);
		
		// direction from headlight toward this fragment
        vec3 fragDir = -lightdir;
        
        // angle between headlight aim and fragment direction
        float cosAngle = dot(normalize(u_spotdirection), fragDir);
        float coscutoff = cos(radians(30.0));   // half of 60 degrees

        if (cosAngle >= coscutoff) {
            intensity = min(1.0, 1.0 / dist);   // inside cone: distance falloff
        } else {
            intensity = 0.0;                    // outside cone: no headlight
        }
    }
			
	
	//difuse
	
	float diffuse = max(dot(normal, lightdir), 0.0) * intensity;
	
	//specular:
	vec3 specular = vec3(0.0);
	if(u_shiny) {
		vec3 view = normalize(u_cameraposition - v_worldpos);
        vec3 reflectDir = reflect(-lightdir, normal);
        float spec = pow(max(dot(reflectDir, view), 0.0), 32.0);
        specular = u_lightColour * spec * intensity;
    }
	
	vec3 lighting = u_ambientColour + u_lightColour * diffuse; //ambient
	
	vec3 texturecolour = texColour.rgb * lighting + specular;  //colour or the texture
	
	o_colour = vec4(texturecolour, texColour.a * u_alpha);
}