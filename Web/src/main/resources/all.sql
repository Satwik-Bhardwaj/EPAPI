##会员游戏设置
#sql("memberGameConfig")
SELECT a.id,a.userName,a.memberType,b.comm,b.ptMy,b.ptUp,a.masterId,a.agentId,a.masterDirect
FROM tb_member_info a JOIN (
	SELECT 'register' configType, b.id memberId, c.`gameId`, c.`comm`,c.ptMy,c.ptUp
	FROM
		tb_member_register_config c
			JOIN tb_member_register_info a on a.id = c.configId
			JOIN tb_member_info b ON (b.masterId = a.masterId and b.masterDirect=1) OR (b.agentId = a.agentId and b.masterDirect=0)
			LEFT JOIN tb_member_config mc ON mc.memberId = b.id AND c.gameId = mc.gameId
			LEFT JOIN tb_member_use_template mut ON mut.memberId = b.id AND mut.gameId = c.gameId AND mut.`status` = 1
	WHERE
		mc.id IS NULL AND mut.memberId is NULL AND b.id=#para(memberId) and  c.`status`=1
	UNION
	SELECT 'template' configType, b.id memberId, c.`gameId`, c.`comm`,c.ptMy,c.ptUp
	FROM
		tb_member_config_template c
			JOIN tb_member_info b ON c.masterDirect = b.masterDirect and (( c.masterDirect=0 and c.agentId = b.agentId ) or (c.masterDirect=1 and b.masterId = c.masterId))
			JOIN tb_member_use_template mut ON mut.memberId = b.id AND mut.gameId = c.gameId AND mut.`status` = 1
			LEFT JOIN tb_member_config a ON a.memberId = b.id  AND c.gameId = a.gameId
	WHERE
		a.id IS NULL AND b.id = #para(memberId) and  c.`status`=1
	UNION
	SELECT 'config' configType, `memberId`, `gameId`, `comm`, ptMy, ptUp
	FROM
		tb_member_config
	WHERE
		memberId = #para(memberId) and `status`=1) b ON a.id=b.memberId
WHERE a.id=#para(memberId) and b.gameId=#para(gameId)
#end

#sql("memberGameConfigApi")
SELECT a.id,a.userName,a.memberType,b.comm,b.ptMy,b.ptUp,a.masterId,a.agentId,a.masterDirect,a.`status`
FROM tb_member_info a JOIN (
	SELECT 'register' configType, b.id memberId, c.`gameId`, c.`comm`,c.ptMy,c.ptUp,c.status
	FROM
		tb_member_register_config c
			JOIN tb_member_register_info a on a.id = c.configId
			JOIN tb_member_info b ON (b.masterId = a.masterId and b.masterDirect=1) OR (b.agentId = a.agentId and b.masterDirect=0)
			LEFT JOIN tb_member_config mc ON mc.memberId = b.id AND c.gameId = mc.gameId
			LEFT JOIN tb_member_use_template mut ON mut.memberId = b.id AND mut.gameId = c.gameId AND mut.`status` = 1
	WHERE
		mc.id IS NULL AND mut.memberId is NULL AND b.id=#para(memberId) and  c.`status`=1
	UNION
	SELECT 'template' configType, b.id memberId, c.`gameId`, c.`comm`,c.ptMy,c.ptUp,c.status
	FROM
		tb_member_config_template c
			JOIN tb_member_info b ON c.masterDirect = b.masterDirect and (( c.masterDirect=0 and c.agentId = b.agentId ) or (c.masterDirect=1 and b.masterId = c.masterId))
			JOIN tb_member_use_template mut ON mut.memberId = b.id AND mut.gameId = c.gameId AND mut.`status` = 1
			LEFT JOIN tb_member_config a ON a.memberId = b.id  AND c.gameId = a.gameId
	WHERE
		a.id IS NULL AND b.id = #para(memberId) and  c.`status`=1
	UNION
	SELECT 'config' configType, `memberId`, `gameId`, `comm`, ptMy, ptUp,status
	FROM
		tb_member_config
	WHERE
			memberId = #para(memberId) and `status`=1) b ON a.id=b.memberId
					  JOIN tb_game_info p ON b.gameId = p.id
WHERE a.id=#para(memberId) and p.api=#para(api) and b.status=#para(status) and b.gameId=#para(gameId)
#end

##白牌的设置
#sql("masterConfig")
WITH RECURSIVE cte_parent ( id,userName, parentId,comm,ptMy,ptRemaining,ptSub,ptForce,ptUp ) AS (
	SELECT a.id,a.userName,a.parentId,b.comm,b.ptMy,b.ptRemaining,b.ptSub,b.ptForce,b.ptUp
	FROM
	tb_master_info a JOIN tb_master_config b ON a.id=b.masterId
	WHERE
	a.id = ?  AND b.gameId=?
	UNION ALL
	SELECT p.id,p.userName,p.parentId,b.comm,b.ptMy,b.ptRemaining,b.ptSub,b.ptForce,b.ptUp
	FROM
	tb_master_info p JOIN tb_master_config b ON p.id=b.masterId
	INNER JOIN cte_parent AS c ON p.id = c.parentId
	WHERE b.gameId=?
	)
SELECT * FROM cte_parent
#end

##代理的设置
#sql("agentConfig")
WITH RECURSIVE cte_parent ( id,userName, masterId, parentId,comm,ptMy,ptRemaining,ptSub,ptForce,ptUp   ) AS (
	SELECT a.id,a.userName,a.masterId,a.parentId,b.comm,b.ptMy,b.ptRemaining,b.ptSub,b.ptForce,b.ptUp
	FROM tb_agent_info a
	JOIN tb_agent_config b ON a.id=b.agentId
	WHERE
	a.id = ?  AND b.gameId = ?
	UNION ALL
	SELECT p.id,p.userName,p.masterId,p.parentId,b.comm,b.ptMy,b.ptRemaining,b.ptSub,b.ptForce,b.ptUp
	FROM
	tb_agent_info p JOIN tb_agent_config b ON p.id=b.agentId
	INNER JOIN cte_parent AS c ON p.id = c.parentId
	WHERE b.gameId = ?
	)
SELECT * FROM cte_parent
#end