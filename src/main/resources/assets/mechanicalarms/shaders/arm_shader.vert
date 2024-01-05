#version 330 compatibility

layout (location = 0) in vec3 in_pos;
layout (location = 1) in vec2 in_texcoord;
layout (location = 2) in vec3 in_normal;
layout (location = 3) in vec2 in_light;
layout (location = 4) in mat4 in_transform;

out vec2 texCoord;
out vec2 lightCoord;
out vec3 fragNorm;
out vec3 fragPos;

uniform mat4 projection;
uniform mat4 view;

void main(){
	gl_Position = projection * view * in_transform * vec4(in_pos, 1);

	fragPos = vec3(in_transform * vec4(in_pos, 1.0));
	fragNorm = mat3(transpose(inverse(in_transform))) * in_normal;

	//0 and 1 are used for the p and q coordinates because p defaults to 0 and q defaults to 1
	texCoord = (gl_TextureMatrix[0] * vec4(in_texcoord, 0, 1)).st;
	lightCoord = (gl_TextureMatrix[1] * vec4(in_light.x * 16, in_light.y * 16, 0, 1)).st;
}