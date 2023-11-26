#version 330 compatibility

in vec2 texCoord;
in vec4 color;
in vec3 lighting;
out vec4 FragColor;

uniform sampler2D texture;
uniform sampler2D lightmap;

void main(){
	vec4 col = color * texture2D(texture, texCoord);
	FragColor = vec4(col.rgb * lighting, col.a);
}