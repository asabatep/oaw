<%@ include file="/common/taglibs.jsp"%>
<%@page import="es.inteco.common.Constants"%>
<html:xhtml />


<!--  JQ GRID   -->
<link rel="stylesheet" href="/oaw/js/jqgrid/css/ui.jqgrid.css">

<link rel="stylesheet" href="/oaw/css/jqgrid.semillas.css">

<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script src="/oaw/js/jqgrid/jquery.jqgrid.src.js"></script>
<script src="/oaw/js/jqgrid/i18n/grid.locale-es.js" type="text/javascript"></script>

<script src="/oaw/js/gridServicioDiagnostico.js" type="text/javascript"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>


<!--  JQ GRID   -->
<script>
	//Buscador
	function buscar() {
		reloadGrid('/oaw/secure/JsonServicioDiagnostico.do?action=search&'
				+ $('#ServicioDiagnosticoForm').serialize());
	}
	
	function exportar() {
		window.location.href ='/oaw/secure/JsonServicioDiagnostico.do?action=export&'+ $('#ServicioDiagnosticoForm').serialize();
	}

	function limpiar() {
		$('#ServicioDiagnosticoForm')[0].reset();
		reloadGrid('/oaw/secure/JsonServicioDiagnostico.do?action=search&'
				+ $('#ServicioDiagnosticoForm').serialize());
		
		
	}

	$(window)
			.on(
					'load',
					function() {

						var $jq = $.noConflict();

						//Primera carga del grid el grid
						$jq(document)
								.ready(
										function() {
											//Desactivar cache de AJAX de forma global
											$.ajaxSetup({
												cache : false
											});

											$('#ServicioDiagnosticoForm')[0]
													.reset();
											reloadGrid('/oaw/secure/JsonServicioDiagnostico.do?action=search&'
													+ $(
															'#ServicioDiagnosticoForm')
															.serialize());
										});

					});
</script>
<!-- servicioDiagnostico.jsp -->
<div id="main">



	<div id="container_menu_izq">
		<jsp:include page="menu.jsp" />
	</div>

	<div id="container_der">

		<div id="migas">
			<p class="sr-only">
				<bean:message key="ubicacion.usuario" />
			</p>
			<ol class="breadcrumb">
				<li class="active"><span class="glyphicon glyphicon-home" aria-hidden="true"></span> <bean:message
						key="migas.diagnostico" /></li>
			</ol>
		</div>

		<div id="cajaformularios">

			<h2>
				<bean:message key="servicio.diagnostico.title" />
			</h2>


			<form id="ServicioDiagnosticoForm" class="formulario form-horizontal" onsubmit="buscar()">
				<fieldset>
					<legend>Buscador</legend>
					<jsp:include page="/common/crawler_messages.jsp" />


					<div class="formItem">
						<label for="usuario" class="control-label"><strong class="labelVisu"><bean:message
									key="servicio.diagnostirco.estadisticas.usuario" /></strong></label> <input type="text" class="texto form-control" id="usuario"
							name="usuario" />
					</div>

					<div class="formItem">
						<label for="url" class="control-label"><strong class="labelVisu"><bean:message
									key="servicio.diagnostirco.estadisticas.url" /></strong></label> <input type="text" class="texto form-control" id="url"
							name="url" />
					</div>


					<div class="formItem">
						<label for="email" class="control-label"><strong class="labelVisu"><bean:message
									key="servicio.diagnostirco.estadisticas.email" /></strong></label> <input type="text" class="texto form-control" id="email"
							name="email" />
					</div>

					<div class="formItem">
						<label for="email" class="control-label"><strong class="labelVisu"><bean:message
									key="servicio.diagnostirco.estadisticas.email" /></strong></label> <input type="text" class="texto form-control" id="email"
							name="email" />
					</div>



					<div class="formItem">
						<label for="profundidad"><strong class="labelVisu"> Profundidad</strong></label> <select id="profundidad"
							name="profundidad" class="texto form-control">
							<option selected="selected" value=""></option>
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
							<option value="9">9</option>
							<option value="10">10</option>
						</select>
					</div>

					<div class="formItem">
						<label for="amplitud"><strong class="labelVisu"> Amplitud</strong></label> <select id="amplitud" name="amplitud"
							class="texto form-control">
							<option selected="selected" value=""></option>
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
							<option value="9">9</option>
							<option value="10">10</option>
						</select>
					</div>

					<div class="formItem">
						<label for="informe"><strong class="labelVisu">Informe</strong></label> <select id="amplitud" name="amplitud"
							class="texto form-control">
							<option selected="selected" value=""></option>
							<option value="informe-obs-5">Accesibilidad</option>
							<option value="informe-obs-4">UNE-EN2019</option>
							<option value="informe-obs-3">UNE-2012 (versi&#243;n 2)</option>
							<option value="informe-obs-2">UNE-2012</option>
							<option value="informe-obs-1">UNE-2004</option>
						</select>
					</div>

					<div class="formItem">
						<label for="amplitud"><strong class="labelVisu">Tipo</strong></label> <select id="informe" name="informe"
							class="texto form-control">
							<option selected="selected" value=""></option>
							<option value="url">URL</option>
							<option value="lista_urls">Lista de URLs</option>
							<option value="c&oacute;digo_fuente">C&oacute;digo_fuente</option>
						</select>
					</div>


					<div class="formItem">
						<label for="amplitud"><strong class="labelVisu">Estado</strong></label> <select id="estado" name="estado"
							class="texto form-control">
							<option selected="selected" value=""></option>
							<option value="launched">Lanzado</option>
							<option value="finished">Finalizado</option>
							<option value="not_crawled">No analizado</option>
							<option value="error">Error</option>
							<option value="missing_params">Par�metros incorrectos</option>
						</select>
					</div>
					
					<div class="formItem">
						<label for="amplitud"><strong class="labelVisu">En directorio</strong></label> <select id="informe" name="informe"
							class="texto form-control">
							<option selected="selected" value=""></option>
							<option value="0">No</option>
							<option value="1">S&#237;</option>

						</select>
					</div>


					<div class="formItem">
						<label for="startDate"><strong class="labelVisu"> <bean:message
									key="servicio.diagnostico.fecha.inicio" /></strong></label>
						<html:text styleClass="textoCorto form-control" name="ServicioDiagnosticoForm" property="startDate"
							styleId="startDate" onkeyup="escBarra(event, document.forms['ServicioDiagnosticoForm'].elements['startDate'], 1)"
							maxlength="10" />
						<span> <img src="../images/boton-calendario.gif"
							onclick="popUpCalendar(this, document.forms['ServicioDiagnosticoForm'].elements['startDate'], 'dd/mm/yyyy')"
							alt="<bean:message key="img.calendario.alt" />" />
						</span>
						<bean:message key="date.format" />
					</div>
					<div class="formItem">
						<label for="endDate"><strong class="labelVisu"><bean:message key="servicio.diagnostico.fecha.fin" /></strong></label>
						<html:text styleClass="textoCorto form-control" name="ServicioDiagnosticoForm" property="endDate"
							styleId="endDate" onkeyup="escBarra(event, document.forms['ServicioDiagnosticoForm'].elements['endDate'], 1)"
							maxlength="10" />
						<span> <img src="../images/boton-calendario.gif"
							onclick="popUpCalendar(this, document.forms['ServicioDiagnosticoForm'].elements['endDate'], 'dd/mm/yyyy')"
							alt="<bean:message key="img.calendario.alt" />" />
						</span>
						<bean:message key="date.format" />
					</div>

					<div class="formButton">
						<span onclick="buscar()" class="btn btn-default btn-lg"> <span class="glyphicon glyphicon-search"
							aria-hidden="true"></span> <bean:message key="boton.buscar" />
						</span> <span onclick="limpiar()" class="btn btn-default btn-lg"> <span aria-hidden="true"></span> <bean:message
								key="boton.limpiar" />
						</span>
						<span onclick="exportar()" class="btn btn-primary btn-lg"> <span aria-hidden="true"></span> <bean:message
								key="boton.exportar" />
						</span>
					</div>
				</fieldset>
			</form>


			<%-- 			<html:form styleClass="formulario" method="post" action="/secure/ServicioDiagnostico.do" 
				onsubmit="return chooseValidation(this)">
				<jsp:include page="/common/crawler_messages.jsp" />

				<fieldset>
					<legend>
						<bean:message key="servicio.diagnostico.fecha.legend" />
					</legend>
					<p>
						<bean:message key="leyenda.campo.obligatorio" />
					</p>
					<div class="formItem">
						<label for="startDate" class="labelCorto"><strong class="labelVisu"><acronym
								title="<bean:message key="campo.obligatorio" />"> * </acronym> <bean:message
									key="servicio.diagnostico.fecha.inicio" />: </strong></label>
						<html:text styleClass="textoCorto" name="ServicioDiagnosticoForm" property="startDate" styleId="startDate"
							onkeyup="escBarra(event, document.forms['ServicioDiagnosticoForm'].elements['startDate'], 1)" maxlength="10" />
						<span> <img src="../images/boton-calendario.gif"
							onclick="popUpCalendar(this, document.forms['ServicioDiagnosticoForm'].elements['startDate'], 'dd/mm/yyyy')"
							alt="<bean:message key="img.calendario.alt" />" />
						</span>
						<bean:message key="date.format" />
					</div>
					<div class="formItem">
						<label for="endDate" class="labelCorto"><strong class="labelVisu"><acronym
								title="<bean:message key="campo.obligatorio" />"> * </acronym> <bean:message
									key="servicio.diagnostico.fecha.fin" />: </strong></label>
						<html:text styleClass="textoCorto" name="ServicioDiagnosticoForm" property="endDate" styleId="endDate"
							onkeyup="escBarra(event, document.forms['ServicioDiagnosticoForm'].elements['endDate'], 1)" maxlength="10" />
						<span> <img src="../images/boton-calendario.gif"
							onclick="popUpCalendar(this, document.forms['ServicioDiagnosticoForm'].elements['endDate'], 'dd/mm/yyyy')"
							alt="<bean:message key="img.calendario.alt" />" />
						</span>
						<bean:message key="date.format" />
					</div>
					<div class="formButton">
						<html:submit property="accion" styleClass="btn btn-primary btn-lg">
							<bean:message key="boton.exportar" />
						</html:submit>
					</div>
				</fieldset>


				<!-- 				JsonResultadoObservatorio -->
				

			</html:form>--%>

			<!-- Grid -->
			<table id="grid"></table>
			<p id="paginador"></p>

			<p id="pCenter">
				<html:link forward="observatoryMenu" styleClass="btn btn-default btn-lg">
					<bean:message key="boton.volver" />
				</html:link>
			</p>
		</div>
		<!-- fin cajaformularios -->
	</div>
</div>