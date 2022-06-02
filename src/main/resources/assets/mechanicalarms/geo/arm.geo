{
	"format_version": "1.12.0",
	"minecraft:geometry": [
		{
			"description": {
				"identifier": "geometry.1",
				"texture_width": 64,
				"texture_height": 64,
				"visible_bounds_width": 4,
				"visible_bounds_height": 3.5,
				"visible_bounds_offset": [0, 1.25, 0]
			},
			"bones": [
				{
					"name": "cage",
					"pivot": [0, 0, 0],
					"cubes": [
						{"origin": [-7, 0, -7], "size": [14, 1, 1], "uv": [28, 33]},
						{"origin": [-7, 14, -7], "size": [14, 1, 1], "uv": [24, 17]},
						{"origin": [-7, 0, 6], "size": [14, 1, 1], "uv": [24, 2]},
						{"origin": [-7, 14, 6], "size": [14, 1, 1], "uv": [24, 0]},
						{"origin": [6, 0, -6], "size": [1, 1, 12], "uv": [14, 33]},
						{"origin": [6, 14, -6], "size": [1, 1, 12], "uv": [0, 32]},
						{"origin": [-7, 0, -6], "size": [1, 1, 12], "uv": [24, 4]},
						{"origin": [-7, 14, -6], "size": [1, 1, 12], "uv": [20, 20]},
						{"origin": [-7, 0, 6], "size": [1, 14, 1], "uv": [4, 45]},
						{"origin": [-7, 0, -7], "size": [1, 14, 1], "uv": [0, 45]},
						{"origin": [6, 0, -7], "size": [1, 14, 1], "uv": [44, 35]},
						{"origin": [6, 0, 6], "size": [1, 14, 1], "uv": [40, 35]}
					]
				},
				{
					"name": "baseXYZ",
					"pivot": [0, 17, 0],
					"cubes": [
						{"origin": [-4, 13, -4], "size": [8, 8, 8], "uv": [0, 16]},
						{"origin": [-4, 15, -10], "size": [4, 4, 6], "uv": [34, 19]}
					]
				},
				{
					"name": "firstZ",
					"parent": "baseXYZ",
					"pivot": [0, 18, -14]
				},
				{
					"name": "core",
					"parent": "firstZ",
					"pivot": [0, 0, 0],
					"cubes": [
						{"origin": [-4, 13, -18], "size": [4, 8, 8], "uv": [0, 0]},
						{"origin": [-4, 21, -17], "size": [4, 1, 6], "uv": [0, 0]},
						{"origin": [-4, 22, -17], "size": [8, 1, 6], "uv": [0, 0]}
					]
				},
				{
					"name": "firstX",
					"parent": "firstZ",
					"pivot": [2, 18, -14],
					"cubes": [
						{"origin": [-4, -1, -18], "size": [8, 8, 8], "uv": [0, 16]},
						{"origin": [0, 7, -16], "size": [4, 6, 4], "uv": [34, 19]}
					]
				},
				{
					"name": "gear",
					"parent": "firstX",
					"pivot": [0, 0, 0],
					"cubes": [
						{"origin": [0, 20, -16], "size": [4, 1, 4], "uv": [0, 0]},
						{"origin": [0, 14, -17], "size": [4, 6, 6], "uv": [0, 0]},
						{"origin": [0, 13, -16], "size": [4, 1, 4], "uv": [0, 0]},
						{"origin": [0, 15, -11], "size": [4, 4, 1], "uv": [0, 0]},
						{"origin": [0, 15, -18], "size": [4, 4, 1], "uv": [0, 0]}
					]
				}
			]
		}
	]
}