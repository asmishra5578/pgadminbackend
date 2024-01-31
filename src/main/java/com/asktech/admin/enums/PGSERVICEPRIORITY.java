package com.asktech.admin.enums;

import java.util.HashMap;
import java.util.Map;

public enum PGSERVICEPRIORITY {
	
	
HIGH(70),LOW(40),MEDIUM(50),HIGHEST(100);
	
	private static Map<Object, Object> map = new HashMap<>();
	private Integer pgSERVICEPRIORITY;
	static {
		for (PGSERVICEPRIORITY pgSERVICEPRIORITY : PGSERVICEPRIORITY.values()) {
			map.put(pgSERVICEPRIORITY.pgSERVICEPRIORITY, pgSERVICEPRIORITY);
		}
	}

	public Integer getValue() {
		return pgSERVICEPRIORITY;
	}


	private PGSERVICEPRIORITY(Integer k) {
		this.pgSERVICEPRIORITY = k;
	}

	public Integer getStatus() {
		return pgSERVICEPRIORITY;
	}


}


