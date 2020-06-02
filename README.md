# Coupled_ABM-Flood_Model_Hamburg
As flood risk is a function of flood hazard and communities' exposure and vulnerability, one way of reducing flood risk is by reducing the vulnerability. Focusing on the physical and economic aspects, measures to reduce vulnerability include elevating houses, retrofitting, dry/wet floodproofing, insurance and subsidies. These measures either prevent flooding or minimise the impact. While measures such as subsidies are offered by authorities or aid groups, the decision to implement most adaptation measures is made at the household level. The factors that affect household adaptation behaviour include flood risk perception, experience with flooding, socioeconomic and geographic factors and reliance on public protection.

We build a coupled agent-based and flood models that comprehensively includes and integrates human and flood attributes to draw new insights for flood risk management (FRM) policy design. We use the protection motivation theory (PMT) (Rogers, 1983) to investigate household-level decision making to adopt mitigation measures against flood threats.

This coupled ABM-Flood model is conceptualised based on the coupled flood-agent-institution modelling framework (CLAIM). CLAIM integrates actors, institutions, the urban environment, hydrologic and hydrodynamic processes and external factors that affect local FRM activities. The framework conceptualizes the complex interaction of floods, humans and their environment as drivers of flood hazard, vulnerability and exposure. A full description of the CLAIM framework can be found in Abebe et al. (2019).

The human subsystem is modelled using the agent-based modelling approach (ABM). As such, it incorporates heterogeneous actors and their actions on and interactions with their environment and flood. It also provides the possibility to analyze the underlying institutions that govern the actions and interactions in managing flood risk by incorporating MAIA (Modelling Agent systems using Institutional Analysis) meta-model (Ghorbani et al., 2013). We use the protection motivation theory (PMT) (Rogers, 1983) to investigate household-level decision making to adopt mitigation measures against flood threats. The flood subsystem is modelled using physically-based, numerical flood modelling software. The ABM is coupled with the flood model dynamically to understand how humans and their environment interact, to experiment the effect of different institutions and to investigate FRM policy options.

The code has five packages: *Collective structure*, *Operational structure*, *Physical structure*, *Aggregate data collector* and *Wilhelmsburg context builder and Global Variables*. The first three packages are based on the MAIA structure – the collective and physical structures create the agent and object classes while the operational structure stores four action situation classes. These classes define the human dynamics (threat and coping appraisals, assessing damage and implementing/abandoning measures). The other two packages are based on the RS architecture. The data collection package implements results collection and creating csv outputs The Wilhelmsburg context builder implements model initialization and the Global Variables class defines and updates agent attributes.

This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (https://creativecommons.org/licenses/by-nc-sa/4.0/).
Below, we provide a full description of the model following the ODD (Overview, Design Concepts, Details) protocol (Grimm et al., 2006; Grimm et al. 2010).

## 1.	Purpose
The purpose of the coupled model is to explore human-flood interactions to better manage flood risk. We test the effects of economic incentives, institutional configurations and individual strategies on household flood adaptation behaviour in Wilhelmsburg, a quarter of Hamburg, Germany. The adaptation behaviour is the implementation of or not implementing primary measures (install utilities in higher storeys, flood adapted interior fittings and flood barriers) and a secondary measure (adapted furnishing). The economic incentive we consider in the model is a subsidy from the state authorities. The institutional configuration is designed assuming households implement adaptation measures based on house categories, and their decision can be affected by their social network. The individual strategies are the average number of years households take to transform a coping behaviour into an action (i.e., measure implementation), the average number of consecutive years a household implements an adaptation measure and a decision to implement a secondary measure. The impact of households’ adaptation behaviour on the overall flood risk is measured by the number of households who develop a coping behaviour, the number of households who implemented measures and the contents and building damages mitigated.

## 2.	Entities, state variables and scales
The entities included in the model are household agents, an authority agent and the urban environment. 
-	The household agents are representations of the residents of Wilhelmsburg. These agents live in residential houses. The actions they pursue include appraising threat and coping, implementing adaptation measure, and assessing direct damage. The agent attributes related to threat appraisal are flood experience, reliance on public protection, perception of climate change and source of information about flooding. The attributes related to coping appraisal are direct flood experience, house ownership and household income. If agents decide to implement an adaptation measure, they know which measure to implement based on the institutions identified. The conceptualization excludes businesses, industries, farmlands and other auxiliary buildings due to lack of data.
-	The authority agent represents the relevant municipal and state authorities that have the mandate to manage flood risk in Wilhelmsburg. This agent does not have a spatial representation in the model. The only action of this agent is to provide subsidies to household agents based on the policy lever defined in the experimental setup of the model. We model subsidy in a more abstract sense that if agents receive a subsidy, they implement an adaptation measure assuming that agents are satisfied with the amount they receive.
-	The Wilhelmsburg quarter that is surrounded by a ring of dykes and walls defines the urban environment (see Figure 1). The household and authority agents live and interact in this environment. In our conceptualisation, we focus only on household behaviour to protect their houses. Therefore, the only physical artefact explicitly included in the conceptual model are residential houses, which spatially represent the household agents. They have geographical location represented using polygon features, as illustrated in Figure 1. These polygons are used to compute the area of the houses. Houses also have types, which are classified based on “the type of building, occupancy of the ground floor and the type of facing of the building.” (Ujeyl and Rose, 2015, pp. 1540006–6). This study includes 31 types of houses, which we group into five categories: single-family houses, bungalows, IBA buildings, garden houses, and apartment/high-rise buildings.

A simulation runs for 50 time steps in which one time step represents one year. We set the temporal scale in years considering that it may take longer time to implement some of the adaptation measures. The special extent of the model environment is shown in Figure 1 (a shapefile of polygon features). When there is flooding in a given year, a flood map (an output of the coupled flood model) represented by a raster file of 2m resolution is overlaid to evaluate whether a house is flooded. 

<p align="center"> <img src="https://user-images.githubusercontent.com/13009562/83541889-8518dd80-a4fa-11ea-8609-d6ea6e8b17e4.png" width="592.8" height="422"> </p>
Figure 1. A map of the study area of Wilhelmsburg. The red polygon shows Wilhelmsburg’s coastal protection ring of dykes and walls. The study focuses on residential housings within the protected area. The buildings shown in the map are only those that are part of the model conceptualisation. The inset maps in the right show the map of Germany (bottom) and Hamburg (top). (Source: the base map is an ESRI Topographic Map)

## 3.	Process overview and Scheduling
The model implementation flow chart shown in Figure 2 lays out the processes agents perform at every time step. First, household agents assess if they perceive flood as a threat. If they do, they appraise coping that leads to protection motivation behaviour. Second, if there is the intention to implement a measure, they implement the adaptation measure based on their house category. Lastly, if there is a flood event at a given time step, the house layer is overlaid with a flood map corresponding to the event. Households check the flood depth at their property and assess the building and contents damages. Agents’ attributes are updated if the actions change their states. These processes are performed until the end of the simulation time. 

<p align="center"> <img src="https://user-images.githubusercontent.com/13009562/83546673-56523580-a501-11ea-88ec-3ef86d844799.png" width="478.4" height="806"> </p>
Figure 2. CLAIM model implementation flowchart for the FRM case of Wilhelmsburg. (a) shows the general flow chart. (b) shows how implementing individual adaptation measures is modelled in the ABM while (c) shows how measures abandoning is modelled. The rest of the actions shown in sub-process shapes in (a) (shapes with double-struck vertical edges) will be described in the Submodels section. In (b) and (c), RN is a random number, p_(adaptation,primary) and p_(adaptation,secondary) are the probabilities of adapting primary and secondary measures, respectively, and p_abandoning is the probability of abandoning a primary or a secondary measure.

## 4.	Design Concepts
**Basic principles** - We use the protection motivation theory (PMT) (Rogers, 1983) to investigate household-level decision making to adopt mitigation measures against flood threats. PMT has three parts – sources of information, cognitive mediating processes and coping modes. The sources of information can be environmental such as seeing what happens to others and intrapersonal such as experience to a similar threat. Triggered by the information, the cognitive mediation process includes the threat and coping appraisals. The threat appraisal evaluates the severity of and the vulnerability to the threat against the intrinsic and extrinsic positive reinforcers. The coping appraisal evaluates the effectiveness of an adaptation measure to mitigate or reduce the risk, the ability to implement the measure, and the associated cost to implement the measure. If the threat and coping appraisals are high, households develop a protection motivation that leads to action. The coping modes can be a single act, repeated acts, multiple acts or repeated, multiple acts.

We have modified the original PMT to use it in an FRM and ABM contexts for the specific case of Wilhelmsburg. In the current study, the sources of information influence the threat appraisal only. We assume that if there is a threat and need to implement a coping measure, the agents know the type of measure they implement based on their house categories. In the case of Wilhelmsburg, the threat appraisal is a function of flood experience, reliance on public protection (i.e., the dyke system), climate change perception and source of information. Whereas, coping appraisal is a function of personal flood experience, house ownership, household income, subsidy from the state and social network.

A list of all the assumptions made during model conceptualization is provided in the appendix of the main paper (citation).

**Emergence** – Flood risk levels emerge from the behaviour of household agents such as how they appraise threat and coping, their decision to implement (or not) measures that reduces their vulnerability and the subsidy levers the authority agent devises. The risk levels are measured by the cumulative number of household agents that: positively appraised coping, positively appraised coping influenced by their social network, implemented primary measures, abandoned primary measures, implemented secondary measures, abandoned secondary measures, and building and contents damage mitigated.

**Adaptation** – The adaptive behaviour of household agents is adopting (or implementing) primary and secondary measures based on their perception of flood as a threat and the 

**Objectives** - 

**Learning** – 

**Prediction** - 

**Sensing** – 

**Interaction** – 

**Stochasticity** – 

**Collectives** -

**Observation** – 

## 5.	Initialization
The coupled ABM-flood model simulation is initialized with 7859 household agents. Due to the lack of available data, some model factors are parametrised based on our expert estimations. Some, however, are based on literature or census data. For example, since the last major flood occurred in 1962 and only 14% of Wilhelmsburg’s residents are older than the age of 65 (according to the 2011 census ), the flood experience attribute of 86% of the agents is randomly initialized as No. The climate change-related thresholds are based on a study on country level concern about climate change in which 44% Germans are “very or extremely worried”, 42% are “somewhat worried” and the remaining 14% are “not at all or not very worried, or does not think climate change is happening” (NatCen Social Research, 2017). However, the study does not directly relate climate change with flooding. According to the 2011 census, in Wilhelmsburg, the share of apartments occupied by the owners was 15% while apartments rented for a residential purpose were 82%. The remaining 3% were vacant. Based on that, in the ABM model, we randomly initialise 15% of the households as owners of the houses they occupy while the remaining 85% as renters, assuming that the 3% vacant apartments can potentially be rented. Finally, since income is considered sensitive information, the data is not readily available. Hence, we randomly initialise 30% of the agents as low-income households and the rest as high-income. The initial (base value) of the model input factors are given in Table 1. In the table, the values of the last seven factors are set in model experimentation (i.e., their values may vary among simulations).

Table 1. List of model input factors and their base values.
<p align="center"> <img src="https://user-images.githubusercontent.com/13009562/83564423-fbc6d280-a51c-11ea-93cb-0a8467d82a9c.png" width="640" height="753"> </p>

## 6.	Input data

## 7.	Submodels


