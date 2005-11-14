package org.seasar.dao.interceptors;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.dao.DaoMetaData;
import org.seasar.dao.DaoMetaDataFactory;
import org.seasar.dao.SqlCommand;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.util.MethodUtil;
import org.seasar.framework.util.NumberConversionUtil;

/**
 * @author higa
 *  
 */
public class S2DaoInterceptor extends AbstractInterceptor {

	private DaoMetaDataFactory daoMetaDataFactory_;

	public S2DaoInterceptor(DaoMetaDataFactory daoMetaDataFactory) {
		daoMetaDataFactory_ = daoMetaDataFactory;
	}

	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		if (!MethodUtil.isAbstract(method)) {
			return invocation.proceed();
		}
		Class targetClass = getTargetClass(invocation);
		DaoMetaData dmd = daoMetaDataFactory_.getDaoMetaData(targetClass);
		SqlCommand cmd = dmd.getSqlCommand(method.getName());
		Object ret = cmd.execute(invocation.getArguments());
		Class retType = method.getReturnType();
		if (retType.isPrimitive()) {
			return NumberConversionUtil.convertPrimitiveWrapper(retType, ret);
		} else if (Number.class.isAssignableFrom(retType)) {
			return NumberConversionUtil.convertNumber(retType, ret);
		}
		return ret;
	}
}