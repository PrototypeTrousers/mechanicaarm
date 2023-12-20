#version 330 compatibility

layout (location = 0) in vec3 in_pos;
layout (location = 1) in vec2 in_texcoord;
layout (location = 2) in vec3 in_normal;
layout (location = 3) in vec2 in_light;
layout (location = 4) in vec4[3] in_transform_quat;
layout (location = 7) in int in_bone_idx;

out vec2 texCoord;
out vec2 lightCoord;
out vec4 color;
out vec3 lighting;

uniform mat4 projection;
uniform mat4 view;

void main(){


	gl_Position = projection * view * in_transform * vec4(in_pos, 1);

	//0 and 1 are used for the p and q coordinates because p defaults to 0 and q defaults to 1
	texCoord = (gl_TextureMatrix[0] * vec4(in_texcoord, 0, 1)).st;
	lightCoord = (gl_TextureMatrix[1] * vec4(in_light.x *16, in_light.y *16, 0, 1)).st;
	color = vec4(4, 4, 4, 4);

	vec3 totalLighting = vec3(gl_LightModel.ambient) * vec3(gl_FrontMaterial.emission);
	vec3 normal = (gl_NormalMatrix * in_normal).xyz;
	vec4 difftot = vec4(0.0F);

	for (int i = 0; i < gl_MaxLights; i ++){

		vec4 diff = gl_FrontLightProduct[i].diffuse * max(dot(normal, gl_LightSource[i].position.xyz), 0.0f);
		diff = clamp(diff, 0.0F, 1.0F);

		difftot += diff;
	}
	lighting = clamp((difftot + gl_LightModel.ambient).rgb, 0.0F, 1.0F);
}