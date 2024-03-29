package com.albert.user.web.controller.auth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.dayatang.querychannel.Page;
import org.openkoala.auth.application.ResourceApplication;
import org.openkoala.auth.application.ResourceTypeApplication;
import org.openkoala.auth.application.vo.ResourceVO;
import org.openkoala.auth.application.vo.RoleVO;
import org.openkoala.koala.auth.ss3adapter.ehcache.CacheUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auth/Resource")
public class ResourceController extends BaseController{

	@Inject
	private ResourceApplication resourceApplication;

	@Inject
	private ResourceTypeApplication resourceTypeApplication;

	@RequestMapping("/list")
	public String list() {
		return "auth/Resource-list";
	}

	@ResponseBody
	@RequestMapping("/pageQueryNotAssignUrlByUser")
	public Page pageQueryNotAssignUrlByUser(ParamsPojo params) {
		String page = params.getPage();
		String pagesize = params.getPagesize();
		RoleVO roleVO = params.getRoleVO();
		int start = Integer.parseInt(page);
		int limit = Integer.parseInt(pagesize);
		Page<ResourceVO> all = resourceApplication.pageQueryNotAssignByRole(start, limit, roleVO);
		return all;
	}

	@ResponseBody
	@RequestMapping("/add")
	public Map<String, Object> add(ParamsPojo params) {
		ResourceVO resourceVO = params.getResourceVO();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		resourceApplication.saveResource(resourceVO);
		CacheUtil.refreshUrlAttributes(resourceVO.getIdentifier());
		dataMap.put("result", "success");
		return dataMap;
	}

	@ResponseBody
	@RequestMapping("/addAndAssignParent")
	public Map<String, Object> addAndAssignParent(ParamsPojo params) {
		ResourceVO childVO = params.getChildVO();
		ResourceVO parentVO = params.getParentVO();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		resourceApplication.saveAndAssignParent(childVO, parentVO);
		CacheUtil.refreshUrlAttributes(childVO.getIdentifier());
		dataMap.put("result", "success");
		return dataMap;
	}

	@ResponseBody
	@RequestMapping("/del")
	public Map<String, Object> del(ParamsPojo params) {
		List<ResourceVO> resourceVOs = params.getResourceVOs();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		for (ResourceVO resourceVO : resourceVOs) {
			resourceApplication.removeResource(resourceVO.getId());
			CacheUtil.refreshUrlAttributes(resourceVO.getIdentifier());
		}
		dataMap.put("result", "success");
		return dataMap;
	}

	/**
	 * 查询某节点下一级子菜单并判断是否有权限
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/update")
	public Map<String, Object> update(ParamsPojo params) {
		ResourceVO resourceVO = params.getResourceVO();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		resourceApplication.updateResource(resourceVO);
		CacheUtil.refreshUrlAttributes(resourceVO.getIdentifier());
		dataMap.put("result", "success");
		return dataMap;
	}

	/**
	 * 资源树
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/findAllReourceTree")
	public Page findAllReourceTree() {
		List<ResourceVO> list = resourceApplication.findResourceTree();
		return new Page(0, list.size(), list);
	}

	@ResponseBody
	@RequestMapping("/findAllResourceType")
	public Map<String, Object> findAllResourceType() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("data", resourceTypeApplication.findResourceType());
		return dataMap;
	}

	@ResponseBody
	@RequestMapping("/isResourceEmpty")
	public Map<String, Object> isResourceEmpty() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("result", resourceApplication.isResourceEmpty());
		return dataMap;
	}
}
