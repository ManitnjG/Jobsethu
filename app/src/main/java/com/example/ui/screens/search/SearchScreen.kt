package com.example.ui.screens.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Job
import com.example.data.model.WorkType
import com.example.ui.components.AiSearchBox
import com.example.ui.components.JobCard
import com.example.ui.theme.JobSetuGoldAccent
import com.example.ui.theme.JobSetuTealSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onJobClick: (Job) -> Unit,
    onApplyClick: (Job) -> Unit,
    initialQuery: String = "",
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initialQuery) {
        if (initialQuery.isNotBlank() && initialQuery != uiState.searchQuery) {
            viewModel.onSearchQueryChange(initialQuery)
            viewModel.executeSearch(initialQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Explore & AI Search",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    if (uiState.filters != SearchFilterState() || uiState.searchQuery.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.onSearchQueryChange("")
                                viewModel.resetFilters()
                            },
                            modifier = Modifier.testTag("reset_search_filters_btn")
                        ) {
                            Text("Reset", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // AI Search Input
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                AiSearchBox(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    onSearch = { viewModel.executeSearch(uiState.searchQuery) },
                    onSuggestionClick = { suggestion ->
                        viewModel.onSearchQueryChange(suggestion)
                        viewModel.executeSearch(suggestion)
                    }
                )
            }

            // AI Natural Intent Explanation Banner (Section 10 & 13)
            if (uiState.parsedIntent != null && uiState.parsedIntent!!.reasoningExplanation.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = JobSetuGoldAccent.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JobSetuGoldAccent.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = JobSetuGoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.parsedIntent!!.reasoningExplanation,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Quick Filter Chips Row (Section 10)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filter: Remote / WFH
                FilterChip(
                    selected = uiState.filters.selectedWorkType == WorkType.REMOTE,
                    onClick = {
                        val current = uiState.filters.selectedWorkType
                        viewModel.updateFilters(
                            uiState.filters.copy(
                                selectedWorkType = if (current == WorkType.REMOTE) null else WorkType.REMOTE
                            )
                        )
                    },
                    label = { Text("WFH / Remote", fontSize = 12.sp) },
                    leadingIcon = if (uiState.filters.selectedWorkType == WorkType.REMOTE) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                )

                // Filter: Chennai
                FilterChip(
                    selected = uiState.filters.selectedCity == "Chennai",
                    onClick = {
                        val current = uiState.filters.selectedCity
                        viewModel.updateFilters(
                            uiState.filters.copy(
                                selectedCity = if (current == "Chennai") "" else "Chennai"
                            )
                        )
                    },
                    label = { Text("Chennai", fontSize = 12.sp) }
                )

                // Filter: Bengaluru
                FilterChip(
                    selected = uiState.filters.selectedCity == "Bengaluru",
                    onClick = {
                        val current = uiState.filters.selectedCity
                        viewModel.updateFilters(
                            uiState.filters.copy(
                                selectedCity = if (current == "Bengaluru") "" else "Bengaluru"
                            )
                        )
                    },
                    label = { Text("Bengaluru", fontSize = 12.sp) }
                )

                // Filter: Freshers (0-1 yr)
                FilterChip(
                    selected = uiState.filters.isFresherOnly,
                    onClick = {
                        viewModel.updateFilters(
                            uiState.filters.copy(isFresherOnly = !uiState.filters.isFresherOnly)
                        )
                    },
                    label = { Text("Fresher (0–1 yr)", fontSize = 12.sp) }
                )

                // Filter: IT Only
                FilterChip(
                    selected = uiState.filters.isItOnly == true,
                    onClick = {
                        val newIt = if (uiState.filters.isItOnly == true) null else true
                        viewModel.updateFilters(uiState.filters.copy(isItOnly = newIt))
                    },
                    label = { Text("IT & Software", fontSize = 12.sp) }
                )

                // Filter: Non-IT / Operations
                FilterChip(
                    selected = uiState.filters.isItOnly == false,
                    onClick = {
                        val newIt = if (uiState.filters.isItOnly == false) null else false
                        viewModel.updateFilters(uiState.filters.copy(isItOnly = newIt))
                    },
                    label = { Text("Non-IT / Ops", fontSize = 12.sp) }
                )

                // Filter: Official Portals Only
                FilterChip(
                    selected = uiState.filters.onlyOfficialPortals,
                    onClick = {
                        viewModel.updateFilters(
                            uiState.filters.copy(onlyOfficialPortals = !uiState.filters.onlyOfficialPortals)
                        )
                    },
                    label = { Text("Official Portals Only", fontSize = 12.sp) }
                )
            }

            // Results count and sort header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${uiState.totalCount} vacancies found",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = {
                        viewModel.updateFilters(
                            uiState.filters.copy(sortByMatch = !uiState.filters.sortByMatch)
                        )
                    }
                ) {
                    Text(
                        text = if (uiState.filters.sortByMatch) "Sort: Best Match" else "Sort: Latest",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Job List or Empty State
            if (uiState.searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No jobs found matching your criteria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try clearing some filters or searching for general terms like 'Operations', 'Software', or 'Chennai'.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.onSearchQueryChange("")
                                viewModel.resetFilters()
                            }
                        ) {
                            Text("Clear Filters")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("search_results_list"),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.searchResults) { job ->
                        JobCard(
                            job = job,
                            onJobClick = onJobClick,
                            onSaveToggle = { viewModel.toggleSave(it) },
                            onApplyClick = onApplyClick,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
