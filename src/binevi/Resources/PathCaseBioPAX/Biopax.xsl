<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xml:space="default" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xs="http://www.w3.org/2001/XMLSchema#" xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:bp="http://www.biopax.org/release/biopax-level1.owl#">
	<xsl:output method="xml" indent="yes" version="1.0" encoding="ISO-8859-1"/>
	<xsl:template match="/">
		<graphdata>
			<molecules>
				<xsl:apply-templates select="//bp:smallMolecule"/>
				<xsl:apply-templates select="//bp:protein"/>
				<xsl:apply-templates select="//bp:dna"/>
				<xsl:apply-templates select="//bp:rna"/>
				<xsl:apply-templates select="//bp:complex"/>
				<xsl:apply-templates select="//bp:cofactor"/>
			</molecules>
			<genericprocesses>
				<xsl:apply-templates select="//bp:pathwayStep"/>
			</genericprocesses>
			<pathways>
				<xsl:for-each select="//bp:pathway">
					<pathway>
						<xsl:attribute name="id"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
						<xsl:attribute name="name"><xsl:value-of select="bp:NAME"/></xsl:attribute>
						<xsl:attribute name="expanded">true</xsl:attribute>
						<xsl:attribute name="organism"><xsl:value-of select="bp:ORGANISM"/></xsl:attribute>
						<genericprocesses>
							<xsl:for-each select="bp:PATHWAY-COMPONENTS">
								<genericprocess>
									<xsl:choose>
										<xsl:when test="@rdf:resource">
											<xsl:variable name="pctemp" select="substring(@rdf:resource,2)"/>
											<xsl:attribute name="id"><xsl:value-of select="$pctemp"/></xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="id"><xsl:value-of select="bp:pathwayStep/@rdf:ID"/></xsl:attribute>
										</xsl:otherwise>
									</xsl:choose>
								</genericprocess>
							</xsl:for-each>
						</genericprocesses>
					</pathway>
				</xsl:for-each>
			</pathways>
		</graphdata>
	</xsl:template>
	<xsl:template match="bp:smallMolecule">
		<molecule>
			<xsl:attribute name="id"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="bp:NAME"/></xsl:attribute>
			<xsl:attribute name="type">smallmolecule</xsl:attribute>
			<xsl:attribute name="iscommon">false</xsl:attribute>
		</molecule>
	</xsl:template>
	<xsl:template match="bp:protein">
		<molecule>
			<xsl:attribute name="id"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="bp:NAME"/></xsl:attribute>
			<xsl:attribute name="type">protein</xsl:attribute>
			<xsl:attribute name="isCommon">false</xsl:attribute>
		</molecule>
	</xsl:template>
	<xsl:template match="bp:dna">
		<molecule>
			<xsl:attribute name="id"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="bp:NAME"/></xsl:attribute>
			<xsl:attribute name="type">dna</xsl:attribute>
			<xsl:attribute name="isCommon">false</xsl:attribute>
		</molecule>
	</xsl:template>
	<xsl:template match="bp:rna">
		<molecule>
			<xsl:attribute name="id"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="bp:NAME"/></xsl:attribute>
			<xsl:attribute name="type">rna</xsl:attribute>
			<xsl:attribute name="isCommon">false</xsl:attribute>
		</molecule>
	</xsl:template>
	<xsl:template match="bp:complex">
		<molecule>
			<xsl:attribute name="id"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="bp:NAME"/></xsl:attribute>
			<xsl:attribute name="type">complex</xsl:attribute>
			<xsl:attribute name="isCommon">false</xsl:attribute>
		</molecule>
	</xsl:template>
	<xsl:template match="bp:pathwayStep">
		<genericprocess>
			<xsl:attribute name="id"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
			<biochemicalreaction>
				<xsl:for-each select="bp:STEP-INTERACTIONS">
					<xsl:choose>
						<xsl:when test="@rdf:resource">
							<xsl:variable name="sitemp" select="substring(@rdf:resource,2)"/>
							<xsl:apply-templates select="//bp:biochemicalReaction[@rdf:ID=$sitemp]">
								<xsl:with-param name="pathwaystepid" select="../@rdf:ID"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:when test="bp:biochemicalReaction">
							<xsl:apply-templates select="bp:biochemicalReaction">
								<xsl:with-param name="pathwaystepid" select="../@rdf:ID"/>
							</xsl:apply-templates>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
			</biochemicalreaction>
			<catalyzes>
				<xsl:for-each select="bp:STEP-INTERACTIONS">
					<xsl:choose>
						<xsl:when test="@rdf:resource">
							<xsl:variable name="sitemp" select="substring(@rdf:resource,2)"/>
							<xsl:apply-templates select="//bp:catalysis[@rdf:ID=$sitemp]"/>
						</xsl:when>
						<xsl:when test="bp:catalysis">
							<xsl:apply-templates select="bp:catalysis"/>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
			</catalyzes>
		</genericprocess>
	</xsl:template>
	<xsl:template match="bp:biochemicalReaction">
		<xsl:param name="pathwaystepid"/>
		<xsl:attribute name="id"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
		<ecnumber>
			<xsl:value-of select="bp:EC-NUMBER"/>
		</ecnumber>
		<name>
			<xsl:value-of select="bp:NAME"/>
		</name>
		<xsl:for-each select="bp:LEFT">
			<molecule>
				<id>
					<xsl:choose>
						<xsl:when test="@rdf:resource">
							<xsl:variable name="leftresource" select="substring(@rdf:resource,2)"/>
							<xsl:choose>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/@rdf:resource">
									<xsl:value-of select="substring(//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:smallMolecule">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:protein">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:rna">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:dna">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:complex">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$leftresource]/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
								</xsl:when>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/@rdf:resource">
							<xsl:value-of select="substring(bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:protein">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:rna">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:dna">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:complex">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
						</xsl:when>
					</xsl:choose>
				</id>
				<role>SUBSTRATE</role>
			</molecule>
		</xsl:for-each>
		<xsl:for-each select="bp:RIGHT">
			<molecule>
				<id>
					<xsl:choose>
						<xsl:when test="@rdf:resource">
							<xsl:variable name="rightresource" select="substring(@rdf:resource,2)"/>
							<xsl:choose>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/@rdf:resource">
									<xsl:value-of select="substring(//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:smallMolecule">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:protein">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:rna">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:dna">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
								</xsl:when>
								<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:complex">
									<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$rightresource]/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
								</xsl:when>
							</xsl:choose>
						</xsl:when>
						<xsl:when test="bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/@rdf:resource">
							<xsl:value-of select="substring(bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:protein">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:rna">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:dna">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
						</xsl:when>
						<xsl:when test="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:complex">
							<xsl:value-of select="//bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
						</xsl:when>
					</xsl:choose>
				</id>
				<role>PRODUCT</role>
			</molecule>
		</xsl:for-each>
		<!-- For modulators -->
		<xsl:for-each select="//bp:pathwayStep[@rdf:ID=$pathwaystepid]/bp:STEP-INTERACTIONS">
			<xsl:choose>
				<xsl:when test="@rdf:resource">
					<xsl:variable name="sitemp" select="substring(@rdf:resource,2)"/>
					<xsl:apply-templates select="//bp:modulation[@rdf:ID=$sitemp]"/>
				</xsl:when>
				<xsl:when test="bp:modulation">
					<xsl:apply-templates select="bp:modulation"/>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="bp:catalysis">
		<catalyze>
			<xsl:attribute name="id"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
			<reversible>
				<xsl:value-of select="bp:DIRECTION"/>
			</reversible>
			<cofactor>
				<xsl:value-of select="substring(bp:COFACTOR/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/@rdf:resource,2)" />
			</cofactor>
			<geneproductmoleculeid>
				<xsl:choose>
					<xsl:when test="bp:CONTROLLER/@rdf:resource">
						<xsl:variable name="controllertemp" select="substring(bp:CONTROLLER/@rdf:resource,2)"/>
						<xsl:choose>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/@rdf:resource">
								<xsl:value-of select="substring(//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:smallMolecule">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:protein">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:rna">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:dna">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:complex">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/@rdf:resource">
								<xsl:value-of select="substring(//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:smallMolecule">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:protein">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:rna">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:dna">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:complex">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/@rdf:resource">
								<xsl:value-of select="substring(bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:protein">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:rna">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:dna">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:complex">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/@rdf:resource">
								<xsl:value-of select="substring(bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:protein">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:rna">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:dna">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:complex">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
							</xsl:when>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</geneproductmoleculeid>
			<processid>
				<xsl:choose>
					<xsl:when test="bp:CONTROLLED/@rdf:resource">
						<xsl:value-of select="substring(bp:CONTROLLED/@rdf:resource,2)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="substring(bp:CONTROLLED/bp:biochemicalreaction/@rdf:ID,2)"/>
					</xsl:otherwise>
				</xsl:choose>
			</processid>
		</catalyze>
	</xsl:template>
	<xsl:template match="bp:modulation">
		<molecule>
			<id>
				<xsl:choose>
					<xsl:when test="bp:CONTROLLER/@rdf:resource">
						<xsl:variable name="controllertemp" select="substring(bp:CONTROLLER/@rdf:resource,2)"/>
						<xsl:choose>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/@rdf:resource">
								<xsl:value-of select="substring(//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:smallMolecule">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:protein">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:rna">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:dna">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:complex">
								<xsl:value-of select="//bp:physicalEntityParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/@rdf:resource">
								<xsl:value-of select="substring(//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:smallMolecule">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:protein">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:rna">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:dna">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:complex">
								<xsl:value-of select="//bp:sequenceParticipant[@rdf:ID=$controllertemp]/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
							</xsl:when>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/@rdf:resource">
								<xsl:value-of select="substring(bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:protein">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:rna">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:dna">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:complex">
								<xsl:value-of select="bp:CONTROLLER/bp:physicalEntityParticipant/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/@rdf:resource">
								<xsl:value-of select="substring(bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/@rdf:resource,2)"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:smallMolecule/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:protein">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:protein/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:rna">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:rna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:dna">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:dna/@rdf:ID"/>
							</xsl:when>
							<xsl:when test="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:complex">
								<xsl:value-of select="bp:CONTROLLER/bp:sequenceParticipant/bp:PHYSICAL-ENTITY/bp:complex/@rdf:ID"/>
							</xsl:when>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</id>
			<catalyzeid>
				<xsl:choose>
					<xsl:when test="bp:CONTROLLED/@rdf:resource">
						<xsl:value-of select="substring(bp:CONTROLLED/@rdf:resource,2)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="substring(bp:CONTROLLED/bp:catalysis/@rdf:ID,2)"/>
					</xsl:otherwise>
				</xsl:choose>
			</catalyzeid>
			<role>
				<xsl:value-of select="bp:CONTROL-TYPE"/>
			</role>
		</molecule>
	</xsl:template>
</xsl:stylesheet>