#version 330 compatibility

in vec2 texCoord;
in vec2 lightCoord;
in vec3 fragNorm;
in vec3 fragPos;

out vec4 FragColor;

uniform sampler2D texture;
uniform sampler2D lightmap;

void main(){

	vec4 color = texture2D(texture, texCoord) * texture2D(lightmap, lightCoord);
	// ambient
	vec3 ambient = 0.2 * color.rgb;
	// diffuse
	vec3 normal = normalize(fragNorm);
	vec3 lightDir = normalize( normal - fragPos);

	float diff = max(dot(lightDir, normal), 0.0);
	vec3 diffuse = diff * color.rgb;
	// specular
	//vec3 viewDir = normalize(viewPos (Eyes, so 0,0,0 ?)- fragPos);
	vec3 viewDir = normalize(-fragPos);
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = 0.0;

	vec3 halfwayDir = normalize(lightDir + viewDir);
	spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0);

	vec3 specular = vec3(0.3) * spec; // assuming bright white light color
	FragColor = vec4(ambient + diffuse + specular, color.a);

}