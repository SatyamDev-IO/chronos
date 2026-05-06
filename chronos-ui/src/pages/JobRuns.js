import React, { useEffect, useState } from "react";
import axios from "axios";

export default function JobRuns({ jobId, goBack }) {
    const [runs, setRuns] = useState([]);

    const fetchRuns = async () => {
        const res = await axios.get(
            `http://localhost:3092/jobs/${jobId}/runs`,
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
            }
        );
        setRuns(res.data);
    };

    useEffect(() => {
        fetchRuns();
    }, []);

    return (
        <div style={{ padding: 20 }}>
            <h2>Job Runs (Job ID: {jobId})</h2>

            <button onClick={goBack}>⬅ Back</button>

            {runs.map((run) => (
                <div key={run.id} style={{ border: "1px solid", margin: 10, padding: 10 }}>
                    <p><b>Status:</b> {run.status}</p>
                    <p><b>Start:</b> {run.startTime}</p>
                    <p><b>End:</b> {run.endTime}</p>
                    <p><b>Output:</b> {run.output}</p>
                    <p><b>Error:</b> {run.error}</p>
                </div>
            ))}
        </div>
    );
}