#version 330 compatibility

in vec2 texCoord;
in vec2 lightCoord;
in vec3 fragNorm;
in vec3 fragPos;

out vec4 FragColor;

uniform sampler2D texture;
uniform sampler2D lightmap;

void main(){
	//vec3 L = normalize(gl_LightSource[0].position.xyz - fragPos);
	vec3 L = normalize(gl_LightSource[0].position.xyz - fragPos);
	vec3 E = normalize(-fragPos); // we are in Eye Coordinates, so EyePos is (0,0,0)
	vec3 R = normalize(-reflect(L,fragNorm));

	//calculate Ambient Term:
	vec4 Iamb = gl_FrontLightProduct[0].ambient;

	//calculate Diffuse Term:
	vec4 Idiff = gl_FrontLightProduct[0].diffuse * max(dot(fragNorm,L), 0.0);
	Idiff = clamp(Idiff, 0.0, 1.0);

	// calculate Specular Term:
	vec4 Ispec = gl_FrontLightProduct[0].specular * pow(max(dot(R,E),0.0),0.3*gl_FrontMaterial.shininess);
	Ispec = clamp(Ispec, 0.0, 1.0);
	// write Total Color:
	FragColor = texture2D(texture, texCoord) * texture2D(lightmap, lightCoord) + Iamb + Idiff + Ispec;
}