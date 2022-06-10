{
	"format_version": "1.12.0",
	"minecraft:geometry": [
		{
			"description": {
				"identifier": "geometry.arm_basic",
				"texture_width": 16,
				"texture_height": 16,
				"visible_bounds_width": 8,
				"visible_bounds_height": 5,
				"visible_bounds_offset": [0, 1.5, 0]
			},
			"bones": [
				{
					"name": "Base",
					"pivot": [0, 8, 0],
					"cubes": [
						{
							"origin": [-7, 16, -7],
							"size": [14, 1, 14],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [1, 14]},
								"east": {"uv": [0, 0], "uv_size": [1, 14]},
								"south": {"uv": [0, 0], "uv_size": [1, 14]},
								"west": {"uv": [0, 0], "uv_size": [1, 14]},
								"up": {"uv": [14, 14], "uv_size": [-14, -14]},
								"down": {"uv": [14, 14], "uv_size": [-14, -14]}
							}
						},
						{
							"origin": [-8, 0, -8],
							"size": [16, 16, 16],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [16, 16]},
								"east": {"uv": [0, 0], "uv_size": [16, 16]},
								"south": {"uv": [0, 0], "uv_size": [16, 16]},
								"west": {"uv": [0, 0], "uv_size": [16, 16]},
								"up": {"uv": [16, 16], "uv_size": [-16, -16]},
								"down": {"uv": [16, 16], "uv_size": [-16, -16]}
							}
						}
					]
				},
				{
					"name": "arm1",
					"pivot": [0, 24, 0],
					"cubes": [
						{
							"origin": [-6, 17, -6],
							"size": [12, 14, 12],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [12, 14]},
								"east": {"uv": [0, 0], "uv_size": [12, 14]},
								"south": {"uv": [0, 0], "uv_size": [12, 14]},
								"west": {"uv": [0, 0], "uv_size": [12, 14]},
								"up": {"uv": [12, 12], "uv_size": [-12, -12]},
								"down": {"uv": [12, 12], "uv_size": [-12, -12]}
							}
						},
						{
							"origin": [-7, 18, -6],
							"size": [14, 12, 12],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [14, 12]},
								"east": {"uv": [0, 0], "uv_size": [12, 12]},
								"south": {"uv": [0, 0], "uv_size": [14, 12]},
								"west": {"uv": [0, 0], "uv_size": [12, 12]},
								"up": {"uv": [14, 12], "uv_size": [-14, -12]},
								"down": {"uv": [14, 12], "uv_size": [-14, -12]}
							}
						},
						{
							"origin": [-4, 20, -22],
							"size": [8, 8, 29],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [12, 12]},
								"east": {"uv": [0, 0], "uv_size": [16, 12]},
								"south": {"uv": [0, 0], "uv_size": [12, 12]},
								"west": {"uv": [0, 0], "uv_size": [16, 12]},
								"up": {"uv": [12, 16], "uv_size": [-12, -16]},
								"down": {"uv": [12, 16], "uv_size": [-12, -16]}
							}
						},
						{
							"origin": [-5, 19, -25],
							"size": [10, 10, 6],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [14, 14]},
								"east": {"uv": [0, 0], "uv_size": [1, 14]},
								"south": {"uv": [0, 0], "uv_size": [14, 14]},
								"west": {"uv": [0, 0], "uv_size": [1, 14]},
								"up": {"uv": [1, 14], "uv_size": [-1, -14]},
								"down": {"uv": [1, 14], "uv_size": [-1, -14]}
							}
						}
					]
				},
				{
					"name": "arm2",
					"parent": "arm1",
					"pivot": [0, 24, -32],
					"cubes": [
						{
							"origin": [-6, 18, -39],
							"size": [12, 12, 14],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [12, 12]},
								"east": {"uv": [0, 0], "uv_size": [12, 14]},
								"south": {"uv": [0, 0], "uv_size": [12, 12]},
								"west": {"uv": [0, 0], "uv_size": [12, 14]},
								"up": {"uv": [12, 14], "uv_size": [-12, -14]},
								"down": {"uv": [12, 14], "uv_size": [-12, -14]}
							}
						},
						{
							"origin": [-7, 18, -38],
							"size": [14, 12, 12],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [14, 12]},
								"east": {"uv": [0, 0], "uv_size": [12, 12]},
								"south": {"uv": [0, 0], "uv_size": [14, 12]},
								"west": {"uv": [0, 0], "uv_size": [12, 12]},
								"up": {"uv": [14, 12], "uv_size": [-14, -12]},
								"down": {"uv": [14, 12], "uv_size": [-14, -12]}
							}
						},
						{
							"origin": [-4, 2, -36],
							"size": [8, 29, 8],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [12, 16]},
								"east": {"uv": [0, 0], "uv_size": [16, 12]},
								"south": {"uv": [0, 0], "uv_size": [12, 16]},
								"west": {"uv": [0, 0], "uv_size": [16, 12]},
								"up": {"uv": [12, 12], "uv_size": [-12, -12]},
								"down": {"uv": [12, 12], "uv_size": [-12, -12]}
							}
						},
						{
							"origin": [-5, -1, -37],
							"size": [10, 6, 10],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [1, 14]},
								"east": {"uv": [0, 0], "uv_size": [1, 14]},
								"south": {"uv": [0, 0], "uv_size": [1, 14]},
								"west": {"uv": [0, 0], "uv_size": [1, 14]},
								"up": {"uv": [14, 14], "uv_size": [-14, -14]},
								"down": {"uv": [14, 14], "uv_size": [-14, -14]}
							}
						}
					]
				},
				{
					"name": "hand",
					"parent": "arm2",
					"pivot": [-8, -5, -32],
					"cubes": [
						{
							"origin": [-3, -8, -28],
							"size": [6, 6, 3],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [14, 14]},
								"east": {"uv": [0, 0], "uv_size": [1, 14]},
								"south": {"uv": [0, 0], "uv_size": [14, 14]},
								"west": {"uv": [0, 0], "uv_size": [1, 14]},
								"up": {"uv": [1, 14], "uv_size": [-1, -14]},
								"down": {"uv": [1, 14], "uv_size": [-1, -14]}
							}
						},
						{
							"origin": [-4, -8, -35],
							"size": [8, 6, 6],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [12, 14]},
								"east": {"uv": [0, 0], "uv_size": [12, 14]},
								"south": {"uv": [0, 0], "uv_size": [12, 14]},
								"west": {"uv": [0, 0], "uv_size": [12, 14]},
								"up": {"uv": [12, 12], "uv_size": [-12, -12]},
								"down": {"uv": [12, 12], "uv_size": [-12, -12]}
							}
						},
						{
							"origin": [-3, -9, -35],
							"size": [6, 8, 6],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [14, 12]},
								"east": {"uv": [0, 0], "uv_size": [12, 12]},
								"south": {"uv": [0, 0], "uv_size": [14, 12]},
								"west": {"uv": [0, 0], "uv_size": [12, 12]},
								"up": {"uv": [14, 12], "uv_size": [-14, -12]},
								"down": {"uv": [14, 12], "uv_size": [-14, -12]}
							}
						},
						{
							"origin": [-3, -8, -36],
							"size": [6, 6, 8],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [12, 12]},
								"east": {"uv": [0, 0], "uv_size": [16, 12]},
								"south": {"uv": [0, 0], "uv_size": [12, 12]},
								"west": {"uv": [0, 0], "uv_size": [16, 12]},
								"up": {"uv": [12, 16], "uv_size": [-12, -16]},
								"down": {"uv": [12, 16], "uv_size": [-12, -16]}
							}
						}
					]
				},
				{
					"name": "claw",
					"parent": "hand",
					"pivot": [0, 0, 0],
					"cubes": [
						{
							"origin": [2, -7, -27],
							"size": [1, 4, 9],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [1, 4]},
								"east": {"uv": [0, 0], "uv_size": [4, 9]},
								"south": {"uv": [0, 0], "uv_size": [1, 4]},
								"west": {"uv": [0, 0], "uv_size": [4, 9]},
								"up": {"uv": [1, 9], "uv_size": [-1, -9]},
								"down": {"uv": [1, 9], "uv_size": [-1, -9]}
							}
						},
						{
							"origin": [-3, -7, -27],
							"size": [1, 4, 9],
							"uv": {
								"north": {"uv": [0, 0], "uv_size": [1, 4]},
								"east": {"uv": [0, 0], "uv_size": [4, 9]},
								"south": {"uv": [0, 0], "uv_size": [1, 4]},
								"west": {"uv": [0, 0], "uv_size": [4, 9]},
								"up": {"uv": [1, 9], "uv_size": [-1, -9]},
								"down": {"uv": [1, 9], "uv_size": [-1, -9]}
							}
						}
					]
				}
			]
		}
	]
}