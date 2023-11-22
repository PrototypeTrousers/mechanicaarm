#version 330 compatibility

layout (location = 0) in vec3 in_pos;
layout (location = 1) in vec2 in_texcoord;
layout (location = 2) in vec3 in_normal;
layout (location = 3) in vec4 in_color;
layout (location = 4) in mat4 in_transform;

out vec2 texCoord;
out vec2 lightCoord;
out vec4 color;
out vec3 lighting;

void main(){
	gl_Position = gl_ModelViewProjectionMatrix * in_transform * vec4(in_pos, 1);

	//0 and 1 are used for the p and q coordinates because p defaults to 0 and q defaults to 1
	texCoord = (gl_TextureMatrix[0] * vec4(in_texcoord, 0, 1)).st;
	lightCoord = (gl_TextureMatrix[1] * gl_MultiTexCoord1).st;
	color = in_color;

	vec3 totalLighting = vec3(gl_LightModel.ambient) * vec3(gl_FrontMaterial.emission);
	vec3 normal = (gl_NormalMatrix * in_normal).xyz;
	vec4 difftot = vec4(0.0F);

	for (int i = 0; i < gl_MaxLights; i ++){

		vec4 diff = gl_FrontLightProduct[i].diffuse * max(dot(normal,gl_LightSource[i].position.xyz), 0.0f);
		diff = clamp(diff, 0.0F, 1.0F);

		difftot += diff;
	}
	lighting = clamp((difftot + gl_LightModel.ambient).rgb, 0.0F, 1.0F);
}