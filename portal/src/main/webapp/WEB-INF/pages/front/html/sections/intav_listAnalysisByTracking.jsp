<%@ include file="/common/taglibs.jsp" %> 
<%@page import="es.inteco.common.Constants"%>
<%@page import="java.util.HashMap"%>
<html:xhtml/>

	<logic:present parameter="<%= Constants.ID_RASTREO %>">
		<bean:parameter id="idrastreo" name="<%= Constants.ID_RASTREO %>"/>
	</logic:present>
	<logic:present parameter="<%= Constants.ID %>">
		<bean:parameter id="id" name="<%= Constants.ID %>"/>
	</logic:present>
	<logic:present parameter="<%= Constants.ID_OBSERVATORIO %>">
		<bean:parameter id="id_observatorio" name="<%= Constants.ID_OBSERVATORIO %>"/>
	</logic:present>
	<logic:present parameter="<%= Constants.ID_EX_OBS %>">
		<bean:parameter id="idExObs" name="<%= Constants.ID_EX_OBS %>"/>
	</logic:present>
	<logic:present parameter="<%= Constants.ID_EX_OBS %>">
    	<bean:parameter id="idCartucho" name="<%= Constants.ID_CARTUCHO %>"/>
    </logic:present>
	
	<jsp:useBean id="paramsVolver" class="java.util.HashMap" />
	<c:set target="${paramsVolver}" property="idrastreo" value="${idrastreo}" />
	<c:set target="${paramsVolver}" property="id_observatorio" value="${id_observatorio}" />
	<c:set target="${paramsVolver}" property="id" value="${id}" />
	<c:set target="${paramsVolver}" property="idExObs" value="${idExObs}" />
	<c:set target="${paramsVolver}" property="idCartucho" value="${idCartucho}"/>
	
	<jsp:useBean id="paramsVolverFC" class="java.util.HashMap" />
	<c:set target="${paramsVolverFC}" property="idrastreo" value="${idrastreo}" />
	<c:set target="${paramsVolverFC}" property="isCliente" value="true" />
	
	<jsp:useBean id="params" class="java.util.HashMap" />
	<c:set target="${params}" property="idrastreo" value="${idrastreo}" />
	<c:set target="${params}" property="id" value="${id}" />
	<c:set target="${params}" property="idCartucho" value="${idCartucho}"/>
	<logic:present parameter="isCliente">
		<c:set target="${params}" property="isCliente" value="true" />
	</logic:present>
	<logic:present parameter="<%= Constants.OBSERVATORY %>">
		<c:set target="${params}" property="observatorio" value="si" />
	</logic:present>

	<div id="migas">
		<p class="oculto"><bean:message key="ubicacion.usuario" /> </p>
		<p><html:link forward="indexAdmin"><bean:message key="migas.inicio" /></html:link> 
		<logic:present parameter="isCliente">
			/ <html:link forward="loadClientCrawlings"><bean:message key="migas.rastreos.cliente" /></html:link>
	 		/ <html:link forward="loadClientFulfilledCrawlings" name="paramsVolverFC"><bean:message key="migas.rastreos.realizados" /></html:link>
		</logic:present>
		<logic:notPresent parameter="isCliente"> 
			<logic:notPresent parameter="<%= Constants.OBSERVATORY %>">
				/ <html:link forward="crawlingsMenu"><bean:message key="migas.rastreo" /></html:link>
			 	/ <html:link forward="loadFulfilledCrawlings" paramId="<%= Constants.ID_RASTREO %>" paramName="<%= Constants.ID_RASTREO %>"><bean:message key="migas.rastreos.realizados" /></html:link>
		 	</logic:notPresent>
		 	<logic:present parameter="<%= Constants.OBSERVATORY %>">
		 		/ <html:link forward="observatoryMenu"><bean:message key="migas.observatorio" /></html:link>
				/ <html:link forward="resultadosPrimariosObservatorio" paramName="id_observatorio" paramId="<%= Constants.ID_OBSERVATORIO %>"><bean:message key="migas.resultado.rastreos.realizados.observatorio" /></html:link>
		 		/ <html:link forward="resultadosObservatorioSemillas" name="paramsVolver"><bean:message key="migas.resultado.observatorio" /></html:link>
		 	</logic:present>
		 </logic:notPresent>
	 	/ <bean:message key="migas.rastreos.realizados.url.analizadas" /></p>
	</div>
	
	<div id="cuerpo">
		<div id="cIzq">&nbsp;</div>
		<div id="contenido">
			<div id="main">
				<h1 class="bulleth1"> <bean:message key="indice.rastreo.gestion.rastreos.realizados"/> </h1>
	
				<div id="cuerpoprincipal">
					
					<div id="container_menu_izq">
						<jsp:include page="menu.jsp"/>
					</div>
					<div id="container_der">
						<div id="cajaformularios">
							<h2 class="config"><bean:message key="search.results.by.tracking"/></h2>
							<logic:notPresent name="<%=Constants.LIST_ANALYSIS %>">	
								<div class="notaInformativaExito">
									<p><bean:message key="indice.rastreo.vacio"/></p>
								</div>
							</logic:notPresent>
							<logic:present name="<%=Constants.LIST_ANALYSIS %>">
								<logic:empty name="<%=Constants.LIST_ANALYSIS %>">	
									<div class="notaInformativaExito">
										<p><bean:message key="indice.rastreo.vacio"/></p>
									</div>
								</logic:empty>
								<logic:notEmpty name="<%=Constants.LIST_ANALYSIS %>">
									<logic:present name="<%= Constants.SCORE %>">
										<div id="observatoryInfo">
											<c:set target="${params}" property="id_observatorio" value="${id_observatorio}" />
											<c:set target="${params}" property="idExObs" value="${idExObs}" />
											<html:link forward="primaryExportPdfAction" name="params" ><img src="../images/icono_pdf.gif" alt="<bean:message key="indice.rastreo.exportar.pdf" />"/></html:link>
											<bean:define id="regeneratePDF"><%= Constants.EXPORT_PDF_REGENERATE %></bean:define>
											<c:set target="${params}" property="${regeneratePDF}" value="true" />
											<html:link forward="primaryExportPdfAction" name="params" ><img src="../images/icono_regenerar_pdf.gif" alt="<bean:message key="indice.rastreo.exportar.pdf.regenerate" />"/></html:link>
											<p>
												<strong><bean:message key="observatorio.nivel.adecuacion"/>: </strong>
												<bean:write name="<%=Constants.SCORE %>" property="level"/>
											</p>
											<p>
												<strong><bean:message key="observatorio.puntuacion.total"/>: </strong>
												<bean:write name="<%=Constants.SCORE %>" property="totalScore"/>
											</p>
											<p>
												<strong><bean:message key="observatorio.puntuacion.nivel.1"/>: </strong>
												<bean:write name="<%=Constants.SCORE %>" property="scoreLevel1"/>
											</p>
											<p>
												<strong><bean:message key="observatorio.puntuacion.nivel.2"/>: </strong>
												<bean:write name="<%=Constants.SCORE %>" property="scoreLevel2"/>
											</p>
										</div>
									</logic:present>
								
									<div class="pag">
										<table>
											<thead>
												<tr>
													<th><bean:message key="search.results.domain"/></th>
													<th><bean:message key="search.results.entity"/></th>
													<th><bean:message key="search.results.date"/></th>
													<logic:notPresent parameter="<%= Constants.OBSERVATORY %>">
														<th><bean:message key="search.results.problems"/></th>
														<th><bean:message key="search.results.warnings"/></th>
														<th><bean:message key="search.results.observations"/></th>
													</logic:notPresent>
													<th><bean:message key="search.results.operations"/></th>
												</tr>
											</thead>
											<tbody>
												<logic:iterate id="analysis" name="<%=Constants.LIST_ANALYSIS %>">
													<c:set target="${params}" property="code" value="${analysis.code}" />
													<tr>
														<logic:empty name="analysis" property="urlTitle">
															<td><bean:write name="analysis" property="url"/></td>
														</logic:empty>
														<logic:notEmpty name="analysis" property="urlTitle">
															<td title ="<bean:write name="analysis" property="urlTitle" />"><bean:write name="analysis" property="url"/></td>
														</logic:notEmpty>
														<logic:empty name="analysis" property="entityTitle">
															<td><bean:write name="analysis" property="entity"/></td>
														</logic:empty>
														<logic:notEmpty name="analysis" property="entityTitle">
															<td title ="<bean:write name="analysis" property="entityTitle" />"><bean:write name="analysis" property="entity"/></td>
														</logic:notEmpty>
														<td><bean:write name="analysis" property="date"/></td>
														<logic:notPresent parameter="<%= Constants.OBSERVATORY %>">
															<logic:equal name="analysis" property="status" value="<%= String.valueOf(Constants.STATUS_SUCCESS) %>">
																<td><bean:write name="analysis" property="problems"/></td>
																<td><bean:write name="analysis" property="warnings"/></td>
																<td><bean:write name="analysis" property="observations"/></td>
																<bean:define id ="title"><bean:message key="view.analyse.title"/> <bean:write name="analysis" property="entity"/> (<bean:write name="analysis" property="date"/>)</bean:define>
																<td><html:link forward="showAnalysisFromCrawler" name="params" title="<%= title %>"><bean:message key="view.analyse"/></html:link></td>
															</logic:equal>
															<logic:equal name="analysis" property="status" value="<%= String.valueOf(Constants.STATUS_ERROR) %>">
																<td>-</td>
																<td>-</td>
																<td>-</td>
																<td><html:img src="../images/error.png" altKey="search.results.analysis.error"/></td>
															</logic:equal>
														</logic:notPresent>
														<logic:present parameter="<%= Constants.OBSERVATORY %>">
															<td>
																<c:set target="${params}" property="id_observatorio" value="${id_observatorio}" />
																<logic:equal name="analysis" property="status" value="<%= String.valueOf(Constants.STATUS_SUCCESS) %>">
																	<html:link forward="showAnalysisFromCrawler" name="params"><img src="../images/list.gif" alt="<bean:message key="indice.rastreo.ver.puntuaci�n" />"/></html:link>
																</logic:equal>
																<logic:equal name="analysis" property="status" value="<%= String.valueOf(Constants.STATUS_ERROR) %>">
																	<html:img src="../images/error.png" altKey="search.results.analysis.error"/>
																</logic:equal>
																<html:link forward="getHtmlSource" name="params"><img src="../images/html_icon.png" alt="<bean:message key="indice.rastreo.ver.codigo.analizado" />"/></html:link>
															</td>
														</logic:present>
													</tr>
												</logic:iterate>
											</tbody>
										</table>
										
										<jsp:include page="pagination.jsp" />
									</div>
								</logic:notEmpty>
							</logic:present>
							<p id="pCenter">
								<logic:notPresent parameter="isCliente"> 
									<logic:notPresent parameter="<%= Constants.OBSERVATORY %>">
									 	<html:link forward="loadFulfilledCrawlings" styleClass="boton" paramId="<%= Constants.ID_RASTREO %>" paramName="<%= Constants.ID_RASTREO %>"><bean:message key="boton.volver" /></html:link>
								 	</logic:notPresent>
								 	<logic:present parameter="<%= Constants.OBSERVATORY %>">
								 		<html:link forward="resultadosObservatorioSemillas" styleClass="boton" name="paramsVolver"><bean:message key="boton.volver" /></html:link>
								 	</logic:present>
								</logic:notPresent>
							</p>
						</div><!-- fin cajaformularios -->
					</div>
				</div><!-- fin CUERPO PRINCIPAL -->
			</div>
		</div>	
	</div> 
