import React, { useEffect, useState } from "react";
import axios from "axios";
import JobRuns from "./JobRuns";

export default function Dashboard() {
    const [jobs, setJobs] = useState([]);
    const [command, setCommand] = useState("");
    const [runAt, setRunAt] = useState("");
    const [interval, setIntervalValue] = useState("");
    const [selectedJobId, setSelectedJobId] = useState(null);
    const [rescheduleTime, setRescheduleTime] = useState({});

    const getAuthHeader = () => ({
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });

    const formatDateTime = (value) => {
        if (!value) return value;
        return value.length === 16 ? value + ":00" : value;
    };

    const fetchJobs = async () => {
        try {
            const res = await axios.get("http://localhost:3092/jobs", getAuthHeader());
            setJobs(res.data);
        } catch (err) {
            console.error(err);
            alert("Failed to fetch jobs");
        }
    };

    const createJob = async () => {
        try {
            await axios.post(
                "http://localhost:3092/jobs",
                {
                    command: command,
                    runAt: runAt
                        ? new Date(runAt).toISOString()
                        : new Date().toISOString(),
                    repeatIntervalSeconds: interval ? parseInt(interval) : null,
                },
                getAuthHeader()
            );

            setCommand("");
            setRunAt("");
            setIntervalValue("");
            fetchJobs();
        } catch (err) {
            console.error(err);
            alert("Failed to create job");
        }
    };

    const deleteJob = async (id) => {
        try {
            await axios.delete(
                `http://localhost:3092/jobs/${id}`,
                getAuthHeader()
            );
            fetchJobs();
        } catch (err) {
            console.error(err);
            alert("Failed to cancel job");
        }
    };

    const rescheduleJob = async (id) => {
        const newTime = rescheduleTime[id];

        if (!newTime) {
            alert("Please select a time");
            return;
        }

        try {
            await axios.put(
                `http://localhost:3092/jobs/${id}/reschedule?newTime=${new Date(newTime).toISOString()}`,
                {},
                getAuthHeader()
            );

            setRescheduleTime({
                ...rescheduleTime,
                [id]: "",
            });

            fetchJobs();
        } catch (err) {
            console.error(err);
            alert("Failed to reschedule job");
        }
    };

    useEffect(() => {
        fetchJobs();
    }, []);

    if (selectedJobId) {
        return (
            <JobRuns
                jobId={selectedJobId}
                goBack={() => setSelectedJobId(null)}
            />
        );
    }

    return (
        <div style={{ padding: 20 }}>
            <h2>Dashboard</h2>

            <button
                onClick={() => {
                    localStorage.removeItem("token");
                    window.location.reload();
                }}
            >
                Logout
            </button>

            <div style={{ marginBottom: 20 }}>
                <input
                    placeholder="Command (e.g. echo hello)"
                    value={command}
                    onChange={(e) => setCommand(e.target.value)}
                />
                <br /><br />

                <input
                    type="datetime-local"
                    value={runAt}
                    onChange={(e) => setRunAt(e.target.value)}
                />
                <br /><br />

                <input
                    placeholder="Repeat Interval (seconds)"
                    value={interval}
                    onChange={(e) => setIntervalValue(e.target.value)}
                />
                <br /><br />

                <button onClick={createJob}>Create Job</button>
            </div>

            <h3>My Jobs</h3>

            {jobs.map((job) => (
                <div
                    key={job.id}
                    style={{
                        border: "1px solid black",
                        margin: 10,
                        padding: 10,
                    }}
                >
                    <p><b>ID:</b> {job.id}</p>
                    <p><b>Command:</b> {job.command}</p>
                    <p><b>Status:</b> {job.status}</p>
                    <p><b>Interval:</b> {job.repeatIntervalSeconds || "None"}</p>
                    <p><b>Next Run:</b> {job.nextRunTime}</p>

                    <input
                        type="datetime-local"
                        value={rescheduleTime[job.id] || ""}
                        onChange={(e) =>
                            setRescheduleTime({
                                ...rescheduleTime,
                                [job.id]: e.target.value,
                            })
                        }
                    />
                    <br /><br />

                    <button onClick={() => setSelectedJobId(job.id)}>
                        View Runs
                    </button>
                    {" "}
                    <button onClick={() => rescheduleJob(job.id)}>
                        Reschedule
                    </button>
                    {" "}
                    <button onClick={() => deleteJob(job.id)}>
                        Cancel
                    </button>
                </div>
            ))}
        </div>
    );
}